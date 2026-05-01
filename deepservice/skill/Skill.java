package deepservice.skill;

import deepservice.model.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Skill {
    private String id;
    private String name;
    private String description;
    private List<String> triggerKeywords;
    private List<Action> actions;
    private int usageCount;
    private long lastUsedAt;

    public Skill() {
        this.id = UUID.randomUUID().toString();
        this.triggerKeywords = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.usageCount = 0;
        this.lastUsedAt = System.currentTimeMillis();
    }

    public Skill(String name, String description, List<String> triggerKeywords, List<Action> actions) {
        this();
        this.name = name;
        this.description = description;
        this.triggerKeywords = triggerKeywords != null ? triggerKeywords : new ArrayList<>();
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTriggerKeywords() {
        return triggerKeywords;
    }

    public void setTriggerKeywords(List<String> triggerKeywords) {
        this.triggerKeywords = triggerKeywords != null ? triggerKeywords : new ArrayList<>();
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedAt = System.currentTimeMillis();
    }

    public void addTriggerKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty() && !triggerKeywords.contains(keyword.toLowerCase())) {
            triggerKeywords.add(keyword.toLowerCase());
        }
    }

    public boolean matches(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return false;
        }
        String lowerInput = userInput.toLowerCase();
        for (String keyword : triggerKeywords) {
            if (lowerInput.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", usageCount=" + usageCount +
                ", triggerKeywords=" + triggerKeywords +
                '}';
    }
}
