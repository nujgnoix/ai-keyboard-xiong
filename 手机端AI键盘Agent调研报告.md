# 手机端AI键盘Agent调研报告

## 执行摘要

本报告对市面上可复用的开源项目进行了全面调研，重点关注在手机端（Android）实现自然语言→键盘指令Agent的方案。

---

## 一、市场热门开源项目盘点

### 1.1 Open-AutoGLM（智谱AI）

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/THUDM/Open-AutoGLM |
| **语言** | Python |
| **架构** | 视觉语言模型（VLM）+ 动作规划器 + ADB执行引擎 |
| **定位** | 面向安卓设备的手机AI代理框架 |

#### 核心能力

- **多模态屏幕理解**：不仅是OCR，而是语义理解
- **自主动作规划**：从自然语言自动拆解为操作序列
- **云边协同**：模型跑在云端，手机负责感知和执行
- **无需Root**：通过ADB协议工作

#### 设计亮点

1. **三层屏幕理解**：
   - 像素层：高分辨率截图
   - 布局层：识别UI组件类型、坐标
   - 语义层：推断功能意图

2. **全闭环执行**：
   - 观察 → 理解 → 规划 → 执行 → 反馈

#### 与我们项目的对比

| 维度 | Open-AutoGLM | 我们项目 |
|------|--------------|---------|
| **输入方式** | 视觉（截图） | 自然语言 + 剪切板内容 |
| **输出** | 屏幕点击/滑动/输入 | 键盘操作（Key Combo）+ 剪切板粘贴 |
| **执行通道** | ADB | 蓝牙HID键盘 |
| **应用场景** | 完整手机操作自动化 | 文本编辑类应用辅助 |

---

### 1.2 DroidClaw

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/unitedbyai/droidclaw |
| **语言** | TypeScript (Bun runtime) |
| **架构** | 感知 → 推理 → 执行 循环 |
| **定位** | 将旧手机变成AI助手 |

#### 核心能力

- **自然语言目标设置**
- **自动处理循环和卡壳检测**
- **视觉回退（截图 + LLM）**
- **多轮对话记忆**
- **本地Ollama支持（完全离线）**

#### 与我们项目的对比

| 维度 | DroidClaw | 我们项目 |
|------|-----------|---------|
| **输入方式** | 无障碍树 / 截图 | 剪切板内容 + 自然语言 |
| **记忆管理** | ✅ 支持 | ❌ 待开发 |
| **执行通道** | ADB | 蓝牙HID键盘 |

---

### 1.3 SeekerClaw

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/sepivip/SeekerClaw |
| **语言** | Kotlin (纯Android) |
| **要求** | Android 14+ |
| **特性** | 71个工具，35+技能，支持Solana钱包 |

#### 核心能力

- **多提供商AI**：Claude + OpenAI + OpenRouter + 兼容API
- **本地运行**：所有功能在设备上执行
- **工具调用**：内置完整的工具系统
- **技能系统**：可扩展的技能框架

#### 我们可以复用的设计

| 功能 | 借鉴点 |
|------|-------|
| **工具抽象** | 类似我们的Action概念 |
| **记忆管理** | 多轮对话记忆机制 |
| **技能系统** | 可插拔的模块化设计 |

---

### 1.4 Agent Droid Bridge (MCP)

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/Neverlow512/agent-droid-bridge |
| **语言** | Python |
| **定位** | MCP Server，把ADB功能暴露给AI Agent |

#### 核心能力

- **14个核心MCP工具**：屏幕捕获、UI检查、触摸、滑动、文本输入、按键事件、应用启动
- **结构化返回**：将XML转换为最小化响应，降低token消耗
- **工具包扩展**：如app_manager添加9个额外工具

#### 借鉴价值

- **剪切板工具设计**：可能我们可以直接复用剪切板操作
- **工具注册模式**：MCP协议是标准

---

### 1.5 Mobileadapt

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/RevylAI/Mobileadapt |
| **语言** | Python |
| **架构** | Appium + UI解析器 + LLM |

#### 核心能力

- **UI结构化表示**：将Android界面转换为HTML-like字符串
- **Mark提示系统**：在截图上叠加元素ID标记
- **Appium集成**：稳定的自动化执行

---

## 二、记忆管理与多轮对话方案

### 2.1 Agents框架（双记忆系统）

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/aiwaves-cn/agents |

#### 记忆架构

```
长期记忆（Long-term Memory）
    └─ 持久化存储对话历史
    └─ 跨会话
    └─ 知识沉淀

短期记忆（Short-term Memory）
    └─ 当前任务即时数据
    └─ 关键摘要
    └─ 上下文筛选
```

#### 内存更新策略

- **定期总结**：历史达到阈值时自动生成摘要
- **相关性筛选**：只保留与当前任务相关内容
- **容量控制**：防止无限增长

---

### 2.2 Memori（开源记忆引擎）

| 特性 | 详情 |
|------|------|
| **GitHub** | https://github.com/GibsonAI/memori |

#### 记忆模式

| 模式 | 说明 |
|------|------|
| **Conscious Mode** | 短期工作记忆，快速访问 |
| **Auto Mode** | 智能搜索，从全数据库找相关内容 |
| **Combined Mode** | 分层，快速+深度检索 |

#### 架构设计

- **多Agent协作**：3个Agent分工（捕获、分析、选择）
- **SQL优先**：SQLite / PostgreSQL / MySQL支持
- **完整文本搜索**

#### 集成示例

```python
from memori import Memori

memori = Memori(conscious_ingest=True)
memori.enable()

# Memori自动记住所有历史
response = completion(
    model="gpt-4",
    messages=[{"role": "user", "content": "继续上次的任务"}]
)
```

---

## 三、我们项目的演进规划

### 3.1 当前单轮架构

```
用户输入自然语言
    ↓
DeepSeek调用 → 生成Action队列
    ↓
Android端蓝牙HID键盘执行
```

#### 现有组件

- DeepSeekClient
- ActionType/Action
- SystemPromptGenerator

---

### 3.2 第二期：剪切板上下文（已规划）

#### 设计方案

```
用户输入自然语言
    ↓
先发送按键：Ctrl-A → Ctrl-C（全选+复制）
    ↓
Android端读取剪切板内容
    ↓
把剪切板内容加入上下文 → 调用DeepSeek
    ↓
生成Action队列 → 执行
```

#### 预期效果

- **编辑类场景**："把前面的'Apple'替换为'Google'" → 先看当前文本
- **格式调整**："给第三段加粗" → 先了解段落结构
- **补全任务**："在结尾加上'谢谢'" → 先看现有结尾

---

### 3.3 第三期：多轮对话与记忆管理

#### 方案选择对比

| 方案 | 优点 | 缺点 |
|------|------|------|
| **简单历史堆栈** | 实现简单，直接推送上下文 | Token消耗大，不智能 |
| **Agents双记忆** | 持久化+摘要，节省token | 依赖外部框架 |
| **Memori集成** | 可插拔，功能完整 | 需要集成 |
| **自研轻量版** | 完全可控，适配我们场景 | 开发工作量大 |

#### 推荐方案

**自研轻量记忆系统**（基于我们现有的架构）

```
MemoryStore（存储层）
    ├─ ShortTermMemory
    │   ├─ 对话历史（最近N条）
    │   ├─ 剪切板快照历史
    │   └─ 执行结果记录
    │
    └─ LongTermMemory
        ├─ 对话摘要
        ├─ 用户偏好（如"喜欢用24号字"）
        └─ 任务模板

SystemPrompt增强
    ├─ 插入上下文历史
    ├─ 插入用户偏好
    └─ 插入相关记忆
```

---

## 四、具体技术实现方案

### 4.1 剪切板内容获取

#### 方案A：Android端复制（推荐）

```kotlin
// Android端在执行AI操作前先执行
fun captureClipboard(): String {
    // 1. 发送Ctrl+A全选
    hidKeyboard.sendKeys(HidKeys.KEY_LEFT_CTRL, HidKeys.KEY_A)

    // 2. 发送Ctrl+C复制
    hidKeyboard.sendKeys(HidKeys.KEY_LEFT_CTRL, HidKeys.KEY_C)

    // 3. 等待并读取剪切板
    delay(100)
    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = clipboardManager.primaryClip

    if (clipData != null && clipData.itemCount > 0) {
        val text = clipData.getItemAt(0).text
        return text?.toString() ?: ""
    }
    return ""
}
```

#### 方案B：通过ADB（备选）

```python
adb shell input keyevent KEYCODE_CTRL_A
adb shell input keyevent KEYCODE_CTRL_C
adb shell pm dump clipboard | grep 'text'
```

---

### 4.2 记忆管理数据结构

```kotlin
// Action记录（用于历史回放）
data class ActionRecord(
    val timestamp: Long,
    val userInput: String,
    val clipboardSnapshot: String,
    val actions: List<Action>,
    val result: String? = null
)

// 用户偏好
data class UserPreferences(
    val fontSize: Int? = null,
    val fontName: String? = null,
    val commonShortcuts: Map<String, String> = emptyMap()
)

// 记忆管理器
class MemoryManager(
    val maxShortTermHistory: Int = 20,
    val maxLongTermSummaries: Int = 50
) {
    val shortTermHistory = mutableListOf<ActionRecord>()
    val longTermSummaries = mutableListOf<String>()
    val preferences = UserPreferences()

    fun addRecord(record: ActionRecord) {
        shortTermHistory.add(record)
        if (shortTermHistory.size > maxShortTermHistory) {
            shortTermHistory.removeAt(0)
            summaryAndMoveToLongTerm()
        }
    }

    private fun summaryAndMoveToLongTerm() {
        // 定期对历史进行总结
        val summary = generateSummary(shortTermHistory)
        longTermSummaries.add(summary)
    }

    fun injectContextPrompt(): String {
        val sb = StringBuilder()
        // 添加最近历史上下文
        shortTermHistory.takeLast(5).forEach {
            sb.append("历史交互：用户说'${it.userInput}'，执行了...\n")
        }
        // 添加用户偏好
        preferences.fontSize?.let { sb.append("用户偏好字号：${it}号\n") }
        return sb.toString()
    }
}
```

---

### 4.3 增强的SystemPrompt生成

```kotlin
fun buildPromptWithMemory(
    userInput: String,
    clipboardContent: String,
    memoryManager: MemoryManager
): String {
    val sb = StringBuilder()
    sb.append("[基础提示词]")
    sb.append("\n\n")

    // 添加剪切板上下文（当前文本）
    if (clipboardContent.isNotEmpty()) {
        sb.append("当前编辑区文本内容（剪切板捕获）：\n")
        sb.append(clipboardContent)
        sb.append("\n\n")
    }

    // 添加记忆上下文
    val memoryContext = memoryManager.injectContextPrompt()
    if (memoryContext.isNotEmpty()) {
        sb.append("历史交互记忆：\n")
        sb.append(memoryContext)
        sb.append("\n\n")
    }

    sb.append("用户指令：$userInput")
    return sb.toString()
}
```

---

## 五、推荐技术复用路线

### 5.1 第一阶段（剪切板上下文）

**复用方案**：

1. **剪切板获取**：
   - 直接在Android端增加剪切板捕获功能（无需第三方库）
   - 参考Agent Droid Bridge的剪切板操作设计

2. **Prompt增强**：
   - 修改SystemPromptGenerator，加入剪切板内容注入

**时间估算**：1-2周

---

### 5.2 第二阶段（多轮对话记忆）

**复用方案**：

1. **自研轻量记忆**：
   - 参考Agents框架的双记忆设计，简化适配我们场景
   - 存储层使用SharedPreferences / SQLite（Android原生）

2. **Memori集成备选**：
   - 如果需求复杂，可集成Memori（Python库→通过API暴露给Kotlin）

**时间估算**：2-3周

---

### 5.3 第三阶段（增强学习）

**复用方向**：

- **SeekerClaw技能系统**：让Agent可以学习新的快捷键组合
- **用户偏好自学习**：自动记忆用户常用字号、格式

---

## 六、技术风险与应对

| 风险 | 说明 | 应对 |
|------|------|------|
| **剪切板敏感数据** | 可能捕获密码等敏感信息 | 设置白名单应用，只处理编辑器 |
| **Token消耗增加** | 加入剪切板和记忆后更长 | 限制剪切板内容长度，摘要记忆 |
| **多平台差异** | Windows vs macOS快捷键 | 延续现有Platform机制 |

---

## 七、总结

### 7.1 我们项目的独特性

| 项目 | 执行方式 | 定位 |
|------|---------|------|
| **Open-AutoGLM/DroidClaw** | ADB屏幕点击 | 完整手机操作 |
| **我们项目** | 蓝牙HID键盘 | 文本编辑类应用辅助 |

**关键优势**：
- 无需ADB权限，任何电脑都能用
- 专注于键盘操作，更精准
- 记忆管理可以提升复杂编辑场景

---

### 7.2 推荐复用清单

| 组件 | 来源 | 优先级 |
|------|------|-------|
| **剪切板操作设计** | Agent Droid Bridge | P0 |
| **记忆系统设计思路** | Agents框架、Memori | P0 |
| **多轮对话机制** | DroidClaw、SeekerClaw | P1 |
| **技能系统架构** | SeekerClaw | P2 |

---

## 八、参考链接

- [Open-AutoGLM GitHub](https://github.com/THUDM/Open-AutoGLM)
- [DroidClaw GitHub](https://github.com/unitedbyai/droidclaw)
- [SeekerClaw GitHub](https://github.com/sepivip/SeekerClaw)
- [Agent Droid Bridge GitHub](https://github.com/Neverlow512/agent-droid-bridge)
- [Agents框架 GitHub](https://github.com/aiwaves-cn/agents)
- [Memori GitHub](https://github.com/GibsonAI/memori)

---

**报告生成时间**：2026-05-01
