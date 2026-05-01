package deepservice.skill;

import java.util.Map;
import java.util.stream.Collectors;

public class UserPreferenceManager {
    private UserPreferences preferences;
    private final int minSamplesForPreference;

    public UserPreferenceManager() {
        this.preferences = new UserPreferences();
        this.minSamplesForPreference = 3;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences != null ? preferences : new UserPreferences();
    }

    public void learnFontSizePreference(int fontSize) {
        if (fontSize <= 0) {
            return;
        }

        int currentPreferred = preferences.getPreferredFontSize();
        int usageCount = preferences.getFontSizeUsageCount();

        int newPreferred = (currentPreferred * usageCount + fontSize) / (usageCount + 1);
        preferences.setPreferredFontSize(newPreferred);
        preferences.incrementFontSizeUsage();
    }

    public void learnShortcutPreference(String action, String shortcut) {
        if (action == null || action.trim().isEmpty() || shortcut == null || shortcut.trim().isEmpty()) {
            return;
        }

        String existingShortcut = preferences.getShortcutForAction(action);
        if (existingShortcut != null && existingShortcut.equals(shortcut)) {
            preferences.incrementShortcutUsage(action);
            return;
        }

        preferences.setShortcutForAction(action, shortcut);
        preferences.incrementShortcutUsage(action);
    }

    public void learnCustomSetting(String key, String value) {
        if (key != null && !key.trim().isEmpty()) {
            preferences.setCustomSetting(key, value);
        }
    }

    public String getPreferencesPrompt() {
        StringBuilder prompt = new StringBuilder();

        if (preferences.getFontSizeUsageCount() >= minSamplesForPreference) {
            prompt.append("用户偏好字体大小: ").append(preferences.getPreferredFontSize()).append("px。\n");
        }

        Map<String, String> stableShortcuts = getStableShortcutPreferences();
        if (!stableShortcuts.isEmpty()) {
            prompt.append("用户常用快捷键:\n");
            for (Map.Entry<String, String> entry : stableShortcuts.entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        Map<String, String> customSettings = preferences.getCustomSettings();
        if (!customSettings.isEmpty()) {
            prompt.append("用户自定义设置:\n");
            for (Map.Entry<String, String> entry : customSettings.entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return prompt.length() > 0 ? prompt.toString() : "";
    }

    private Map<String, String> getStableShortcutPreferences() {
        return preferences.getShortcutPreferences().entrySet().stream()
                .filter(entry -> {
                    Integer count = preferences.getShortcutUsageCount().get(entry.getKey());
                    return count != null && count >= minSamplesForPreference;
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public int getPreferredFontSize() {
        return preferences.getPreferredFontSize();
    }

    public String getShortcutForAction(String action) {
        return preferences.getShortcutForAction(action);
    }

    public String getCustomSetting(String key) {
        return preferences.getCustomSetting(key);
    }

    public void resetPreferences() {
        this.preferences = new UserPreferences();
    }

    public boolean hasLearnedPreferences() {
        return preferences.getFontSizeUsageCount() >= minSamplesForPreference ||
                getStableShortcutPreferences().size() > 0 ||
                !preferences.getCustomSettings().isEmpty();
    }

    public Map<String, Object> getPreferenceStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("preferredFontSize", preferences.getPreferredFontSize());
        stats.put("fontSizeSamples", preferences.getFontSizeUsageCount());
        stats.put("shortcutCount", preferences.getShortcutPreferences().size());
        stats.put("stableShortcutCount", getStableShortcutPreferences().size());
        stats.put("customSettingsCount", preferences.getCustomSettings().size());
        stats.put("hasLearnedPreferences", hasLearnedPreferences());
        return stats;
    }
}
