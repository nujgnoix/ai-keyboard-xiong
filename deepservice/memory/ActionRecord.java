package deepservice.memory;

import deepservice.model.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionRecord {
    private long timestamp;
    private String userInput;
    private String clipboardSnapshot;
    private List<Action> actions;
    private String result;

    public ActionRecord() {
        this.timestamp = System.currentTimeMillis();
        this.actions = new ArrayList<>();
    }

    public ActionRecord(String userInput) {
        this();
        this.userInput = userInput;
    }

    public ActionRecord(String userInput, String clipboardSnapshot) {
        this(userInput);
        this.clipboardSnapshot = clipboardSnapshot;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getClipboardSnapshot() {
        return clipboardSnapshot;
    }

    public void setClipboardSnapshot(String clipboardSnapshot) {
        this.clipboardSnapshot = clipboardSnapshot;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    public void addAction(Action action) {
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(action);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean hasResult() {
        return result != null && !result.isEmpty();
    }

    public boolean hasClipboardSnapshot() {
        return clipboardSnapshot != null && !clipboardSnapshot.isEmpty();
    }

    public String getFormattedTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public String toString() {
        return "ActionRecord{" +
                "timestamp=" + getFormattedTimestamp() +
                ", userInput='" + userInput + '\'' +
                ", actionsCount=" + (actions != null ? actions.size() : 0) +
                ", hasResult=" + hasResult() +
                '}';
    }
}
