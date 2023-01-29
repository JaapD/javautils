package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class NotFoundInLdapRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -3130832669493312250L;

    public NotFoundInLdapRuntimeException(String message) {
        super(message);
    }
}
