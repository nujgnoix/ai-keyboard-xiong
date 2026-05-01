package deepservice.memory;

import deepservice.model.Action;
import deepservice.model.Platform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MemoryManager {
    private static final int SHORT_TERM_CAPACITY = 20;
    private static final int LONG_TERM_CAPACITY = 50;
    private static final int SUMMARY_THRESHOLD = 10;

    private final LinkedList<ActionRecord> shortTermHistory;
    private final LinkedList<String> longTermSummaries;
    private UserPreferences preferences;
    private int recordCountSinceLastSummary;

    public MemoryManager() {
        this.shortTermHistory = new LinkedList<>();
        this.longTermSummaries = new LinkedList<>();
        this.preferences = new UserPreferences();
        this.recordCountSinceLastSummary = 0;
    }

    public MemoryManager(Platform platform) {
        this();
        this.preferences.setPreferredPlatform(platform);
    }

    public void addRecord(ActionRecord record) {
        if (record == null) {
            return;
        }

        if (shortTermHistory.size() >= SHORT_TERM_CAPACITY) {
            ActionRecord oldest = shortTermHistory.removeFirst();
            summarizeRecord(oldest);
        }

        shortTermHistory.addLast(record);
        recordCountSinceLastSummary++;

        if (recordCountSinceLastSummary >= SUMMARY_THRESHOLD) {
            summaryAndMoveToLongTerm();
        }
    }

    public String injectContextPrompt() {
        StringBuilder context = new StringBuilder();

        context.append("=== 用户上下文信息 ===\n\n");

        context.append("【用户偏好设置】\n");
        context.append("- 首选平台: ").append(preferences.getPreferredPlatform().getValue()).append("\n");
        if (preferences.hasFontSize()) {
            context.append("- 字体大小: ").append(preferences.getFontSize()).append("\n");
        }
        if (preferences.hasFontName()) {
            context.append("- 字体名称: ").append(preferences.getFontName()).append("\n");
        }
        if (preferences.hasShortcuts()) {
            context.append("- 常用快捷键: ").append(preferences.getShortcutsDescription()).append("\n");
        }
        context.append("\n");

        if (!longTermSummaries.isEmpty()) {
            context.append("【历史对话摘要】\n");
            int summaryCount = Math.min(5, longTermSummaries.size());
            for (int i = longTermSummaries.size() - summaryCount; i < longTermSummaries.size(); i++) {
                context.append("- ").append(longTermSummaries.get(i)).append("\n");
            }
            context.append("\n");
        }

        if (!shortTermHistory.isEmpty()) {
            context.append("【近期对话记录】\n");
            int startIndex = Math.max(0, shortTermHistory.size() - 5);
            for (int i = startIndex; i < shortTermHistory.size(); i++) {
                ActionRecord record = shortTermHistory.get(i);
                context.append("[").append(record.getFormattedTimestamp()).append("] ");
                context.append("用户: ").append(record.getUserInput()).append("\n");
                if (record.hasResult()) {
                    context.append("  结果: ").append(truncateText(record.getResult(), 100)).append("\n");
                }
            }
            context.append("\n");
        }

        if (!shortTermHistory.isEmpty()) {
            ActionRecord latestRecord = shortTermHistory.getLast();
            if (latestRecord.hasClipboardSnapshot()) {
                context.append("【当前剪贴板内容】\n");
                context.append(truncateText(latestRecord.getClipboardSnapshot(), 500)).append("\n\n");
            }
        }

        context.append("=== 上下文信息结束 ===\n");

        return context.toString();
    }

    public void summaryAndMoveToLongTerm() {
        if (shortTermHistory.isEmpty()) {
            return;
        }

        String summary = generateSummary();
        if (summary != null && !summary.isEmpty()) {
            if (longTermSummaries.size() >= LONG_TERM_CAPACITY) {
                longTermSummaries.removeFirst();
            }
            longTermSummaries.addLast(summary);
        }

        recordCountSinceLastSummary = 0;
    }

    private String generateSummary() {
        if (shortTermHistory.isEmpty()) {
            return null;
        }

        StringBuilder summary = new StringBuilder();
        summary.append("在最近").append(shortTermHistory.size()).append("次交互中，");

        List<String> userIntents = new ArrayList<>();
        for (ActionRecord record : shortTermHistory) {
            String intent = extractIntent(record.getUserInput());
            if (intent != null && !userIntents.contains(intent)) {
                userIntents.add(intent);
            }
        }

        if (!userIntents.isEmpty()) {
            summary.append("用户主要进行了: ");
            summary.append(String.join("、", userIntents.subList(0, Math.min(3, userIntents.size()))));
        }

        return summary.toString();
    }

    private String extractIntent(String userInput) {
        if (userInput == null || userInput.isEmpty()) {
            return null;
        }

        String lowerInput = userInput.toLowerCase();
        if (lowerInput.contains("复制") || lowerInput.contains("粘贴") || lowerInput.contains("剪贴板")) {
            return "剪贴板操作";
        } else if (lowerInput.contains("格式化") || lowerInput.contains("整理")) {
            return "文本格式化";
        } else if (lowerInput.contains("快捷键") || lowerInput.contains("快捷")) {
            return "快捷键操作";
        } else if (lowerInput.contains("搜索") || lowerInput.contains("查找")) {
            return "搜索查找";
        } else if (lowerInput.contains("打开") || lowerInput.contains("启动")) {
            return "打开应用";
        }

        return "其他操作";
    }

    private void summarizeRecord(ActionRecord record) {
        if (record == null) {
            return;
        }

        String summary = "历史记录: " + truncateText(record.getUserInput(), 50);
        if (longTermSummaries.size() >= LONG_TERM_CAPACITY) {
            longTermSummaries.removeFirst();
        }
        longTermSummaries.addLast(summary);
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    public List<ActionRecord> getShortTermHistory() {
        return new ArrayList<>(shortTermHistory);
    }

    public List<String> getLongTermSummaries() {
        return new ArrayList<>(longTermSummaries);
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences != null ? preferences : new UserPreferences();
    }

    public void updatePlatform(Platform platform) {
        this.preferences.setPreferredPlatform(platform);
    }

    public void clearShortTermHistory() {
        shortTermHistory.clear();
        recordCountSinceLastSummary = 0;
    }

    public void clearLongTermSummaries() {
        longTermSummaries.clear();
    }

    public void clearAll() {
        clearShortTermHistory();
        clearLongTermSummaries();
    }

    public int getShortTermHistorySize() {
        return shortTermHistory.size();
    }

    public int getLongTermSummariesSize() {
        return longTermSummaries.size();
    }

    public ActionRecord getLatestRecord() {
        return shortTermHistory.isEmpty() ? null : shortTermHistory.getLast();
    }

    public List<ActionRecord> getRecentRecords(int count) {
        List<ActionRecord> result = new ArrayList<>();
        int startIndex = Math.max(0, shortTermHistory.size() - count);
        for (int i = startIndex; i < shortTermHistory.size(); i++) {
            result.add(shortTermHistory.get(i));
        }
        return result;
    }

    @Override
    public String toString() {
        return "MemoryManager{" +
                "shortTermHistorySize=" + shortTermHistory.size() +
                ", longTermSummariesSize=" + longTermSummaries.size() +
                ", preferences=" + preferences +
                '}';
    }
}
