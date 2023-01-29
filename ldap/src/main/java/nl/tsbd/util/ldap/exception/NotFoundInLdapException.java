package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class NotFoundInLdapException extends Exception {
    private static final long serialVersionUID = -6444662697879534327L;

    public NotFoundInLdapException(String message) {
        super(message);
    }
}
