package deepservice.model;

public enum ActionType {
    KEY_COMBO("key_combo"),
    CLIPBOARD_PASTE("clipboard_paste");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActionType fromString(String text) {
        for (ActionType t : ActionType.values()) {
            if (t.value.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return KEY_COMBO;
    }
}
