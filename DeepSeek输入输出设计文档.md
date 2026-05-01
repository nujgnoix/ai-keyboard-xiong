# DeepSeek 输入输出设计文档

> 创建日期：2026-04-30
> 版本：v1.1
> 关联文档：蓝牙键盘 Android APP 技术文档 v1.0 MVP，自然语言控制电脑可行性调研报告 v1.0

---

## 一、项目背景

我们正在开发一款 **Android 蓝牙 HID 键盘 APP**，其核心能力是将手机模拟为蓝牙键盘，远程操控电脑。

**现有能力**：用户在手机上输入文字 → APP 通过蓝牙 HID 发送按键指令到电脑。由于蓝牙 HID 只能发送 ASCII 按键码，中文输入依赖"手机剪贴板 → 第三方输入法跨设备同步 → 电脑剪贴板 → Ctrl+V 粘贴"的方案。

**本次新增能力**：引入 DeepSeek 大模型，让用户可以用**自然语言**描述操作意图（如"文字放大""搜索天气"），由 DeepSeek 将自然语言转换为结构化的键盘指令序列，再通过蓝牙 HID 发送给电脑执行。

**本文档的读者**：负责开发 DeepSeek 输入输出中转模块的开发者。你需要了解：APP 发给 DeepSeek 什么、DeepSeek 返回什么、客户端如何解析这些指令。

**端到端流程概览**：

```
用户输入自然语言 → APP 调用 DeepSeek API → DeepSeek 返回结构化键盘指令 → 客户端解析并执行
```

---

## 二、核心概念

### 2.1 两种操作类型

蓝牙 HID 键盘只能发送标准 ASCII 按键码，无法直接发送中文、日文等非 ASCII 字符。因此 DeepSeek 生成的键盘指令必须区分两种类型：

| 操作类型 | 适用场景 | 客户端行为 |
|----------|---------|-----------|
| **key_combo** | 快捷键、功能键、ASCII 字符输入 | 发送按键指令 |
| **clipboard_paste** | 中文、特殊符号、长文本输入 | 写入剪贴板，等待同步，发送 Ctrl+V |

### 2.2 案例说明

用户说 **"打开浏览器新标签页，搜索天气"**，DeepSeek 需要输出：

```
步骤 1: Ctrl+T          → key_combo
步骤 2: Ctrl+L          → key_combo
步骤 3: 输入"天气"       → clipboard_paste
步骤 4: Enter           → key_combo
```

---

## 三、DeepSeek API 调用

### 3.1 请求结构

```json
{
  "model": "deepseek-v4-flash",
  "temperature": 0.1,
  "max_tokens": 1000,
  "response_format": { "type": "json_object" },
  "messages": [
    { "role": "system", "content": "<System Prompt>" },
    { "role": "user", "content": "<用户自然语言指令>" }
  ]
}
```

### 3.2 System Prompt

System Prompt 定义了 DeepSeek 的行为规则和输出格式约束。

```
你是一个键盘指令转换助手。用户会用自然语言描述他们想在电脑上执行的操作，你需要将其转换为结构化的键盘指令序列。

## 目标平台：{PLATFORM}

### 平台快捷键规则（必须严格遵守）
- 当前目标平台是 {PLATFORM}，你必须使用该平台的标准快捷键
- Windows: 主修饰键使用 ctrl（如 Ctrl+C 复制、Ctrl+V 粘贴、Ctrl+Z 撤销）
- macOS: 主修饰键使用 cmd（如 Cmd+C 复制、Cmd+V 粘贴、Cmd+Z 撤销）
- macOS 的"全选"是 Cmd+A，"保存"是 Cmd+S
- macOS 的"新建标签页"是 Cmd+T，"关闭标签页"是 Cmd+W
- macOS 的"查找"是 Cmd+F，"撤销"是 Cmd+Z，"重做"是 Cmd+Shift+Z
- macOS 的"切换应用"是 Cmd+Tab
- 粘贴操作：Windows 使用 Ctrl+V，macOS 使用 Cmd+V

## 输出格式
你必须输出合法的 JSON，格式如下：
{
  "actions": [
    {
      "type": "key_combo" | "clipboard_paste",
      "keys": ["ctrl", "t"],
      "text": "要粘贴的文本（仅 clipboard_paste 类型需要）",
      "description": "操作描述"
    }
  ]
}

## 操作类型规则

### type: key_combo
用于所有标准键盘按键操作：
- 快捷键组合（如 Ctrl+C, Cmd+V, Alt+Tab）
- 单个按键（如 Enter, Escape, Tab）
- ASCII 字符输入（如 a-z, 0-9, 标点符号）
修饰键和普通键在同一个 keys 数组中表示"同时按下"。

### type: clipboard_paste
用于所有非 ASCII 文本的输入：
- 中文字符（如"天气"、"搜索"）
- 日文、韩文等非拉丁字符
- 特殊 Unicode 符号
- 任何无法用单个键盘按键表达的文本
此类型必须提供 text 字段。

## 判断规则
- 如果内容只包含 ASCII 可打印字符且长度较短（<=3个字符），使用 key_combo
- 如果内容包含中文、日文、韩文等非 ASCII 字符，使用 clipboard_paste
- 如果内容是纯 ASCII 但长度较长（>3个字符），优先使用 clipboard_paste
- 快捷键操作永远使用 key_combo

## keys 数组规范
修饰键（小写）: ctrl, shift, alt, cmd
普通键: a-z, 0-9, enter, space, tab, escape, backspace, delete, home, end, page_up, page_down, f1-f12, up, down, left, right, =, -, [, ], \, ;, ', ,, ., /, print_screen, scroll_lock, pause, insert

## 只输出 JSON，不要输出任何其他内容。
```

---

## 四、输出格式

### 4.1 JSON Schema

```json
{
  "type": "object",
  "properties": {
    "actions": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "type": { "type": "string", "enum": ["key_combo", "clipboard_paste"] },
          "keys": { "type": "array", "items": { "type": "string" } },
          "text": { "type": "string" },
          "description": { "type": "string" }
        },
        "required": ["type", "description"]
      }
    }
  },
  "required": ["actions"]
}
```

### 4.2 Action 结构说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 是 | `key_combo` 或 `clipboard_paste` |
| keys | array | key_combo必填 | 按键列表，如 `["ctrl", "t"]` |
| text | string | clipboard_paste必填 | 要粘贴的文本 |
| description | string | 是 | 操作描述，用于日志调试 |

---

## 五、输出示例

### 示例 1：文字放大

**输入**：`"文字全部放大"`

**Windows 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["ctrl", "="], "description": "放大页面" }
  ]
}
```

**macOS 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["cmd", "="], "description": "放大页面" }
  ]
}
```

### 示例 2：搜索天气

**输入**：`"打开浏览器新标签页，搜索天气"`

**Windows 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["ctrl", "t"], "description": "新建标签页" },
    { "type": "key_combo", "keys": ["ctrl", "l"], "description": "聚焦地址栏" },
    { "type": "clipboard_paste", "text": "天气", "description": "输入搜索词" },
    { "type": "key_combo", "keys": ["enter"], "description": "执行搜索" }
  ]
}
```

**macOS 输出**：将 `keys` 中的 `ctrl` 替换为 `cmd`。

### 示例 3：新建文档输入中文标题

**输入**：`"新建一个文档，输入标题'周报'，保存"`

**Windows 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["ctrl", "n"], "description": "新建文档" },
    { "type": "clipboard_paste", "text": "周报", "description": "输入标题" },
    { "type": "key_combo", "keys": ["ctrl", "s"], "description": "保存文档" }
  ]
}
```

**macOS 输出**：将 `keys` 中的 `ctrl` 替换为 `cmd`。

### 示例 4：重做操作

**输入**：`"重做上一步操作"`

**Windows 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["ctrl", "y"], "description": "重做" }
  ]
}
```

**macOS 输出**：
```json
{
  "actions": [
    { "type": "key_combo", "keys": ["cmd", "shift", "z"], "description": "重做" }
  ]
}
```

> 注意：这不是简单的 `ctrl` → `cmd` 替换，Windows 用 `Ctrl+Y`，macOS 用 `Cmd+Shift+Z`，按键组合完全不同。

### 示例 5：终端命令

**输入**：`"在终端输入 ls -la 查看文件"`

**输出**（Windows/macOS 相同）：
```json
{
  "actions": [
    { "type": "clipboard_paste", "text": "ls -la", "description": "输入命令" },
    { "type": "key_combo", "keys": ["enter"], "description": "执行命令" }
  ]
}
```

> 说明：`ls -la` 长度 > 3 个字符，优先使用 clipboard_paste。

---

## 六、客户端职责

### 6.1 DeepSeek 负责
- 语义理解（自然语言 → 结构化指令）
- 操作排序（决定 action 的执行顺序）

### 6.2 客户端负责
- 调用 DeepSeek API
- 解析 JSON 响应
- 执行 action（按键、粘贴）
- 管理延迟和时序

### 6.3 延迟管理建议

| 操作 | 建议延迟 |
|------|---------|
| 按键按下到释放 | 10ms |
| 两个 action 之间 | 100ms |
| 粘贴操作后 | 300ms |
| 新建标签页后 | 200ms |

---

## 七、异常处理

| 异常情况 | 处理方式 |
|----------|---------|
| JSON 解析失败 | 提示用户"指令解析失败，请重试" |
| API 超时 | 30 秒超时，提示用户重试 |
| API 返回错误 | 根据错误码处理（限流/余额不足等） |
| action 数量过多 | 限制最多 50 个 action |

---

## 八、总结

| 设计决策 | 结论 |
|----------|------|
| 操作类型 | `key_combo` + `clipboard_paste` |
| 平台信息 | DeepSeek 负责输出正确的平台快捷键 |
| 延迟管理 | 全部由客户端负责 |
| 输出格式 | JSON + System Prompt |
| 温度参数 | 0.1 |
