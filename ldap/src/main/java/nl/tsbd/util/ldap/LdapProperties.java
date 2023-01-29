package nl.tsbd.util.ldap;

public class LdapProperties {
    public final String ldapHost;
    public final int ldapPort;
    public final String adminDn;
    public final String adminPw;
    public final String root;
    public final boolean isTls;

    public LdapProperties(String ldapHost, int ldapPort, String adminDn, String adminPw, String root, boolean isTls) {
        this.ldapHost = ldapHost;
        this.ldapPort = ldapPort;
        this.adminDn = adminDn;
        this.adminPw = adminPw;
        this.root = root;
        this.isTls = isTls;
    }
}
