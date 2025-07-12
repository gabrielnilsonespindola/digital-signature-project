package digital.signature.project.services.exceptions;

public class DigitalSignatureException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DigitalSignatureException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DigitalSignatureException(String msg) {
        super(msg);
    }
}