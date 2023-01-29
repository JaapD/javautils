package nl.tsbd.util.ldap.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class LoginFailedException extends Exception{

    public LoginFailedException(String message) {
        super(message);
    }
}
