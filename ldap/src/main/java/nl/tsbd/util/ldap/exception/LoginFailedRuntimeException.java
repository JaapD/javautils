package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class LoginFailedRuntimeException extends RuntimeException {

    public LoginFailedRuntimeException(String message) {
        super(message);
    }
}
