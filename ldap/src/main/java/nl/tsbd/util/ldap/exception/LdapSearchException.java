package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class LdapSearchException extends RuntimeException {
    public LdapSearchException(String messaga) {
        super(messaga);
    }

    public LdapSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
