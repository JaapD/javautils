package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ModifyPasswordException extends Exception {
    public ModifyPasswordException(String message) {
        super(message);
    }
}
