# AI Keyboard

A pure Java implementation of natural language to keyboard command conversion module.

## Overview

This module takes natural language input and converts it into structured keyboard actions, either as key combinations or clipboard paste operations.

**Features:**
- Natural language to keyboard shortcut conversion
- Cross-platform support (Windows/macOS)
- Auto cursor reset after formatting operations
- Support for both key combos and text pasting

## Architecture

**Work flow:**
1. User inputs natural language (e.g., "Bold the first three words")
2. DeepSeek API analyzes and returns structured actions array
3. Android client executes actions via existing Bluetooth HID logic

## Quick Start

### 1. Copy the `deepservice` package

Copy the `deepservice/` directory to your Android project:

```
app/src/main/java/
```

### 2. Add dependencies

```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
}
```

### 3. Configure API Key

Add your DeepSeek API key in `local.properties`:

```properties
deepseek.api.key=sk-xxxxxxxxxxxxxxxx
```

### 4. Use in your code

```java
DeepSeekClient client = new DeepSeekClient(
    BuildConfig.DEEPSEEK_API_KEY,
    Platform.WINDOWS // or Platform.MAC
);

List<Action> actions = client.execute("Bold the first three words");
```

## Project Structure

```
deepservice/
├── DeepSeekClient.java           # Core API client
├── model/
│   ├── Platform.java
│   ├── ActionType.java
│   └── Action.java
├── prompt/
│   └── SystemPromptGenerator.java
└── exception/
    ├── DeepSeekAPIException.java
    └── ActionParseException.java
```

## Documentation

| Document | Path |
|----------|------|
| Integration Guide | [技术交接文档.md](file:///workspace/技术交接文档.md) |
| Prompt Rules | [SystemPrompt提示词.md](file:///workspace/SystemPrompt提示词.md) |
| Shortcut Reference | [快捷键对照表.md](file:///workspace/快捷键对照表.md) |
| Test Cases | [测试用例.md](file:///workspace/测试用例.md) |

## License

MIT License
