package digital.signature.project.services.exceptions;

public class VerificationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public VerificationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VerificationException(String msg) {
        super(msg);
    }
}
