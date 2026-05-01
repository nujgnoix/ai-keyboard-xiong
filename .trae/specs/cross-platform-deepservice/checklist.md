# Checklist - DeepSeek 中转服务 (Java)

## Phase 1: 项目结构初始化

- [ ] Task 1.1: 项目结构
  - [ ] `deepservice/` 目录及子目录已创建
  - [ ] `build.gradle` 配置文件已创建
  - [ ] `build.gradle` 包含 okhttp 和 gson 依赖

- [ ] Task 1.2: 异常类
  - [ ] `DeepSeekAPIException.java` 包含错误码和消息字段
  - [ ] `ActionParseException.java` 包含原始响应字段

- [ ] Task 1.3: 数据模型
  - [ ] `Platform.java` 枚举包含 WINDOWS, MACOS
  - [ ] `ActionType.java` 枚举包含 KEY_COMBO, CLIPBOARD_PASTE
  - [ ] `Action.java` 包含 type, keys, text, description 字段
  - [ ] `HidCodeMapping.java` 包含完整的键名到HID Code映射

## Phase 2: 核心功能开发

- [ ] Task 2.1: System Prompt生成器
  - [ ] `SystemPromptGenerator.generate(Platform)` 根据平台返回正确模板
  - [ ] Windows模板使用ctrl作为主修饰键
  - [ ] macOS模板使用cmd作为主修饰键
  - [ ] 模板包含完整的快捷键规则说明（基于设计文档）
  - [ ] 模板包含操作类型规则
  - [ ] 模板包含keys数组规范

- [ ] Task 2.2: HID Report构建工具
  - [ ] `HidReportBuilder.toModifierByte(List<String> keys)` 正确计算修饰键字节
  - [ ] `HidReportBuilder.toKeycode(String key)` 正确转换普通键
  - [ ] `HidReportBuilder.buildReport(byte modifier, byte keycode)` 返回8字节数组
  - [ ] `HidReportBuilder.keysToReport(List<String> keys)` 返回完整的HID Report

- [ ] Task 2.3: DeepSeek API服务
  - [ ] `DeepSeekService(String apiKey, String platform)` 正确初始化
  - [ ] `setPlatform(String platform)` 支持动态切换平台
  - [ ] `execute(String instruction)` 返回 `List<Action>`
  - [ ] API调用超时设置为30秒
  - [ ] `parseResponse(String responseText)` 使用gson解析JSON

## Phase 3: 测试

- [ ] Task 3.1: 单元测试覆盖
  - [ ] `HidCodeMappingTest` 测试HID映射表完整性
  - [ ] `SystemPromptGeneratorTest` 测试Windows/macOS Prompt生成
  - [ ] `HidReportBuilderTest` 测试HID Report构建
  - [ ] `DeepSeekServiceTest` 测试API调用逻辑（使用mock）

## Integration Checkpoints

- [ ] 所有Java类无语法错误
- [ ] 数据模型与DeepSeek设计文档规格一致
- [ ] System Prompt包含设计文档中的所有规则
- [ ] 代码符合Java规范

## Acceptance Criteria

- [ ] `DeepSeekService service = new DeepSeekService("api-key", "windows")` 可正常初始化
- [ ] `List<Action> actions = service.execute("文字放大")` 返回正确格式
- [ ] `HidReportBuilder.keysToReport(Arrays.asList("ctrl", "="))` 返回正确的8字节Report
