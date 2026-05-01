# DeepSeek 中转服务 Spec (Java版)

## Why
用户需要一款Android蓝牙HID键盘APP，通过DeepSeek大模型将自然语言转换为键盘指令。中转服务作为Android App内部的纯Java模块被调用，与APP无缝集成。

## What Changes

- 创建Java核心库，实现DeepSeek API调用和指令解析
- 实现System Prompt模板管理，支持Windows/macOS平台适配
- 实现Action数据模型和解析逻辑
- 实现HID Report格式转换工具函数
- 提供简洁的Java API接口供Android App调用

## Impact

- 新增能力：DeepSeek中转服务核心模块
- 新增代码：[`deepservice/`](file:///workspace/deepservice/) 目录下的Java代码
- 依赖项：Android SDK (okhttp, gson)

## ADDED Requirements

### Requirement: DeepSeek API 调用
系统 SHALL 提供 DeepSeek API 调用功能，支持发送自然语言指令并接收结构化的键盘指令序列。

#### Scenario: 正常调用
- **WHEN** 调用方传入自然语言指令（如"打开浏览器新标签页，搜索天气"）
- **AND** 配置了有效的DeepSeek API密钥
- **AND** 指定了目标平台（windows/macos）
- **THEN** 返回List<Action>类型，包含有序的键盘指令序列

#### Scenario: API调用失败
- **WHEN** DeepSeek API返回错误或超时
- **THEN** 抛出DeepSeekAPIException异常

### Requirement: System Prompt 动态生成
系统 SHALL 根据目标平台动态生成System Prompt，包含正确的快捷键映射规则。

#### Scenario: Windows平台
- **WHEN** 平台参数设置为"windows"
- **THEN** System Prompt使用ctrl作为主修饰键

#### Scenario: macOS平台
- **WHEN** 平台参数设置为"macos"
- **THEN** System Prompt使用cmd作为主修饰键

### Requirement: Action 数据模型
系统 SHALL 定义Action数据结构，支持key_combo和clipboard_paste两种操作类型。

## MODIFIED Requirements

无

## REMOVED Requirements

无

## Technical Architecture

```
┌─────────────────────────────────────────────────────────┐
│              deepservice/                              │
├─────────────────────────────────────────────────────────┤
│  model/                                               │
│    ├ Platform.java          │ 平台枚举               │
│    ├ ActionType.java        │ 操作类型枚举           │
│    ├ Action.java            │ Action数据类           │
│    └ HidCodeMapping.java    │ HID映射表              │
│  prompt/                                              │
│    └ SystemPromptGenerator.java │ Prompt生成器         │
│  service/                                             │
│    └ DeepSeekService.java    │ 核心API服务           │
│  exception/                                           │
│    ├ DeepSeekAPIException.java                        │
│    └ ActionParseException.java                        │
│  util/                                                │
│    └ HidReportBuilder.java     │ HID Report构建工具    │
└─────────────────────────────────────────────────────────┘
```

## API Design

### 核心接口

```java
public class DeepSeekService {

    public DeepSeekService(String apiKey, String platform) {
        // 初始化服务
    }

    public List<Action> execute(String instruction) throws DeepSeekAPIException {
        // 发送指令，返回Action列表
    }

    public void setPlatform(String platform) {
        // 动态切换平台
    }
}
```

### 数据模型

```java
public enum Platform {
    WINDOWS("windows"),
    MACOS("macos");
}

public enum ActionType {
    KEY_COMBO("key_combo"),
    CLIPBOARD_PASTE("clipboard_paste");
}

public class Action {
    private ActionType type;
    private List<String> keys;
    private String text;
    private String description;

    // getters, setters
}
```

### 使用示例

```java
DeepSeekService service = new DeepSeekService(
    "sk-xxx",
    Platform.WINDOWS.getValue()
);

List<Action> actions = service.execute("文字全部放大");
for (Action action : actions) {
    Log.d("DeepSeek", action.getDescription());
}
```

## Dependencies (build.gradle)

```gradle
dependencies {
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

## 安全考虑

- API密钥通过构造函数传入，不硬编码
- 支持API调用频率限制配置
- 不在日志中输出敏感信息
