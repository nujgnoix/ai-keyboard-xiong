package deepservice.memory;

import deepservice.model.Platform;

import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private Integer fontSize;
    private String fontName;
    private Map<String, String> commonShortcuts;
    private Platform preferredPlatform;

    public UserPreferences() {
        this.commonShortcuts = new HashMap<>();
        this.preferredPlatform = Platform.WINDOWS;
    }

    public UserPreferences(Platform preferredPlatform) {
        this();
        this.preferredPlatform = preferredPlatform;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Map<String, String> getCommonShortcuts() {
        return commonShortcuts;
    }

    public void setCommonShortcuts(Map<String, String> commonShortcuts) {
        this.commonShortcuts = commonShortcuts != null ? commonShortcuts : new HashMap<>();
    }

    public void addShortcut(String action, String shortcut) {
        if (this.commonShortcuts == null) {
            this.commonShortcuts = new HashMap<>();
        }
        this.commonShortcuts.put(action, shortcut);
    }

    public String getShortcut(String action) {
        return commonShortcuts != null ? commonShortcuts.get(action) : null;
    }

    public void removeShortcut(String action) {
        if (commonShortcuts != null) {
            commonShortcuts.remove(action);
        }
    }

    public Platform getPreferredPlatform() {
        return preferredPlatform;
    }

    public void setPreferredPlatform(Platform preferredPlatform) {
        this.preferredPlatform = preferredPlatform;
    }

    public boolean hasFontSize() {
        return fontSize != null;
    }

    public boolean hasFontName() {
        return fontName != null && !fontName.isEmpty();
    }

    public boolean hasShortcuts() {
        return commonShortcuts != null && !commonShortcuts.isEmpty();
    }

    public String getShortcutsDescription() {
        if (!hasShortcuts()) {
            return "无常用快捷键设置";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : commonShortcuts.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "fontSize=" + fontSize +
                ", fontName='" + fontName + '\'' +
                ", commonShortcuts=" + commonShortcuts +
                ", preferredPlatform=" + preferredPlatform +
                '}';
    }
}
