package nl.tsbd.utils.auth;

public class UnAuthenticatedException extends RuntimeException {
    public UnAuthenticatedException(String message) {
        super(message);
    }
}
