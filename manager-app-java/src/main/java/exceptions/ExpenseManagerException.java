package exceptions;

/** Base unchecked exception for expected expense manager errors. */
public class ExpenseManagerException extends RuntimeException {
    public ExpenseManagerException(String message) {
        super(message);
    }

    public ExpenseManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
