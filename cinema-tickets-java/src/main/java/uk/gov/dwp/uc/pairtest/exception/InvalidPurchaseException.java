package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {

    
    private String errorCode;

    // Keep this for your existing tests to pass
    public InvalidPurchaseException(String message) {
        super(message);
    }

    // Add this for the "Senior" requirement
    public InvalidPurchaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
