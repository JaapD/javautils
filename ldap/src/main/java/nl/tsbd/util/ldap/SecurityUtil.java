package nl.tsbd.util.ldap;

import org.apache.commons.codec.digest.UnixCrypt;

import java.security.SecureRandom;

public class SecurityUtil {
    private static final int SALT_LENGTH = 2;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALLOWED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getSalt2() {
        return getString(SALT_LENGTH);
    }

    public static String getString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ALLOWED.charAt(RANDOM.nextInt(ALLOWED.length())));
        }
        return result.toString();
    }

    public static String crypt(String thepassword) {
        return "{CRYPT}" + UnixCrypt.crypt(thepassword, getSalt2());
    }
}
