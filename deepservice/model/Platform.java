package deepservice.model;

public enum Platform {
    WINDOWS("windows"),
    MACOS("macos");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Platform fromString(String text) {
        for (Platform p : Platform.values()) {
            if (p.value.equalsIgnoreCase(text)) {
                return p;
            }
        }
        return WINDOWS;
    }
}
