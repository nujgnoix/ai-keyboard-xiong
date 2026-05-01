package deepservice.model;

public class ClipboardContext {
    private final String content;
    private final long timestamp;
    private final String source;

    public ClipboardContext(String content) {
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.source = "unknown";
    }

    public ClipboardContext(String content, String source) {
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public int getLength() {
        return content != null ? content.length() : 0;
    }

    public String getTruncatedContent(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...[已截断，共" + content.length() + "字符]";
    }

    @Override
    public String toString() {
        return "ClipboardContext{" +
                "length=" + getLength() +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                '}';
    }
}
