package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ModifyLdapException extends RuntimeException{
    public ModifyLdapException(String message) {
        super(message);
    }
}
