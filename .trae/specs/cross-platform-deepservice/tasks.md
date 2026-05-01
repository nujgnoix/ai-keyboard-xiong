# Tasks

## Phase 1: 项目结构初始化

- [ ] Task 1.1: 创建Java项目结构
  - [ ] 创建 `deepservice/` 目录
  - [ ] 创建 `deepservice/model/` 子目录
  - [ ] 创建 `deepservice/prompt/` 子目录
  - [ ] 创建 `deepservice/service/` 子目录
  - [ ] 创建 `deepservice/exception/` 子目录
  - [ ] 创建 `deepservice/util/` 子目录
  - [ ] 创建 `build.gradle` 配置文件

- [ ] Task 1.2: 创建异常类
  - [ ] 创建 `deepservice/exception/DeepSeekAPIException.java`
  - [ ] 创建 `deepservice/exception/ActionParseException.java`

- [ ] Task 1.3: 创建数据模型
  - [ ] 创建 `deepservice/model/Platform.java` 枚举
  - [ ] 创建 `deepservice/model/ActionType.java` 枚举
  - [ ] 创建 `deepservice/model/Action.java` 数据类
  - [ ] 创建 `deepservice/model/HidCodeMapping.java` 映射表

## Phase 2: 核心功能开发

- [ ] Task 2.1: 实现System Prompt生成器
  - [ ] 创建 `deepservice/prompt/SystemPromptGenerator.java`
  - [ ] 实现Windows平台Prompt模板
  - [ ] 实现macOS平台Prompt模板
  - [ ] 实现 `generate(Platform platform)` 方法

- [ ] Task 2.2: 实现HID Report构建工具
  - [ ] 创建 `deepservice/util/HidReportBuilder.java`
  - [ ] 实现 `toModifierByte(List<String> keys)` 方法
  - [ ] 实现 `toKeycode(String key)` 方法
  - [ ] 实现 `buildReport(byte modifier, byte keycode)` 方法
  - [ ] 实现 `keysToReport(List<String> keys)` 方法

- [ ] Task 2.3: 实现DeepSeek API服务
  - [ ] 创建 `deepservice/service/DeepSeekService.java`
  - [ ] 实现构造函数 `DeepSeekService(String apiKey, String platform)`
  - [ ] 实现 `setPlatform(String platform)` 方法
  - [ ] 实现 `execute(String instruction)` 方法
  - [ ] 实现内部 `callApi(List<Message> messages)` 方法
  - [ ] 实现内部 `parseResponse(String responseText)` 方法
  - [ ] 配置超时（30秒）

## Phase 3: 测试

- [ ] Task 3.1: 创建单元测试
  - [ ] 创建 `deepservice/model/HidCodeMappingTest.java`
  - [ ] 创建 `deepservice/prompt/SystemPromptGeneratorTest.java`
  - [ ] 创建 `deepservice/util/HidReportBuilderTest.java`
  - [ ] 创建 `deepservice/service/DeepSeekServiceTest.java`

## Task Dependencies

```
Task 1.1 → Task 1.2 → Task 1.3
                ↓
             Task 2.1
                ↓
        Task 2.2 → Task 2.3
                ↓
             Task 3.1
```
