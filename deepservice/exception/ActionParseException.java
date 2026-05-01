package deepservice.exception;

public class ActionParseException extends Exception {
    private String rawResponse;

    public ActionParseException(String message, String rawResponse) {
        super(message);
        this.rawResponse = rawResponse;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
