package deepservice.skill;

import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private int preferredFontSize;
    private int fontSizeUsageCount;
    private Map<String, String> shortcutPreferences;
    private Map<String, Integer> shortcutUsageCount;
    private Map<String, String> customSettings;

    public UserPreferences() {
        this.preferredFontSize = 14;
        this.fontSizeUsageCount = 0;
        this.shortcutPreferences = new HashMap<>();
        this.shortcutUsageCount = new HashMap<>();
        this.customSettings = new HashMap<>();
    }

    public int getPreferredFontSize() {
        return preferredFontSize;
    }

    public void setPreferredFontSize(int preferredFontSize) {
        this.preferredFontSize = preferredFontSize;
    }

    public int getFontSizeUsageCount() {
        return fontSizeUsageCount;
    }

    public void setFontSizeUsageCount(int fontSizeUsageCount) {
        this.fontSizeUsageCount = fontSizeUsageCount;
    }

    public Map<String, String> getShortcutPreferences() {
        return shortcutPreferences;
    }

    public void setShortcutPreferences(Map<String, String> shortcutPreferences) {
        this.shortcutPreferences = shortcutPreferences != null ? shortcutPreferences : new HashMap<>();
    }

    public Map<String, Integer> getShortcutUsageCount() {
        return shortcutUsageCount;
    }

    public void setShortcutUsageCount(Map<String, Integer> shortcutUsageCount) {
        this.shortcutUsageCount = shortcutUsageCount != null ? shortcutUsageCount : new HashMap<>();
    }

    public Map<String, String> getCustomSettings() {
        return customSettings;
    }

    public void setCustomSettings(Map<String, String> customSettings) {
        this.customSettings = customSettings != null ? customSettings : new HashMap<>();
    }

    public void incrementFontSizeUsage() {
        this.fontSizeUsageCount++;
    }

    public void incrementShortcutUsage(String action) {
        if (action != null) {
            shortcutUsageCount.put(action, shortcutUsageCount.getOrDefault(action, 0) + 1);
        }
    }

    public String getShortcutForAction(String action) {
        return action != null ? shortcutPreferences.get(action) : null;
    }

    public void setShortcutForAction(String action, String shortcut) {
        if (action != null && shortcut != null) {
            shortcutPreferences.put(action, shortcut);
        }
    }

    public String getCustomSetting(String key) {
        return key != null ? customSettings.get(key) : null;
    }

    public void setCustomSetting(String key, String value) {
        if (key != null) {
            customSettings.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "preferredFontSize=" + preferredFontSize +
                ", fontSizeUsageCount=" + fontSizeUsageCount +
                ", shortcutPreferences=" + shortcutPreferences +
                ", customSettings=" + customSettings +
                '}';
    }
}
