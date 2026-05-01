package deepservice;

import deepservice.model.Action;
import deepservice.model.Platform;
import deepservice.prompt.SystemPromptGenerator;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== DeepSeek 客户端 Demo ===\n");

        System.out.println("1. 测试 System Prompt 生成 (Windows):");
        String windowsPrompt = SystemPromptGenerator.generate(Platform.WINDOWS);
        System.out.println(windowsPrompt.substring(0, 300) + "...\n");

        System.out.println("2. 使用示例:");
        System.out.println("   DeepSeekClient client = new DeepSeekClient(\"your-api-key\", \"windows\");");
        System.out.println("   List<Action> actions = client.execute(\"文字放大\");");
        System.out.println("   for (Action action : actions) {");
        System.out.println("       System.out.println(action.getType());  // key_combo / clipboard_paste");
        System.out.println("       System.out.println(action.getDescription());");
        System.out.println("       if (action.getKeys() != null) System.out.println(action.getKeys());");
        System.out.println("       if (action.getText() != null) System.out.println(action.getText());");
        System.out.println("   }");
        System.out.println();
        System.out.println("3. 注意: 解析出的 Action 直接传给已有的蓝牙HID客户端即可!");
    }
}
