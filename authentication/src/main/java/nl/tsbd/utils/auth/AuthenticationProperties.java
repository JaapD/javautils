package nl.tsbd.utils.auth;

import nl.tsbd.util.properties.PropertiesNameEnum;
import nl.tsbd.util.properties.PropertiesReader;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Class to read the authentication properties.
 * This class should be overridden to provide the system property name for the properties file.
 */
public abstract class AuthenticationProperties extends PropertiesReader implements Serializable {

    public enum PropertyNames implements PropertiesNameEnum {
        LdapAdminDn("ldap.admindn"),
        LdapAdminPw("ldap.adminpw"),
        LdapHost("ldap.ldaphost"),
        LdapPort("ldap.ldapport"),
        LdapRoot("ldap.root"),
        LdapTls("ldap.is.tls"),
        ShouldHaveGroup("should.have.group"),
        AdminGroup("admin.group");
        private final String name;

        PropertyNames(String name) {
            this.name = name;
        }

        @Override
        public String getPropertyName() {
            return name;
        }
    }

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    @Override
    protected PropertiesNameEnum[] getPropertyValues() {
        return PropertyNames.values();
    }
}
