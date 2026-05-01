package deepservice.exception;

public class DeepSeekAPIException extends Exception {
    private String errorCode;
    private String errorMessage;

    public DeepSeekAPIException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public DeepSeekAPIException(String errorCode, String errorMessage) {
        super(errorCode + ": " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
