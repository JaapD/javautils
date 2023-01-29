package nl.tsbd.util.ldap;

import nl.tsbd.util.ldap.exception.ModifyLdapException;
import org.junit.jupiter.api.BeforeAll;

abstract class LdapIntegrationTest {

    protected static LdapProperties ldapProperties;

    @BeforeAll
    static void setupProperties() throws ModifyLdapException {
        ldapProperties = new LdapProperties("localhost", 389, "cn=admin,dc=test,dc=nl", "adminPassword","dc=test,dc=nl", false);
        ldapProperties = new LdapProperties("localhost", 389, "cn=admin,dc=tsbd,dc=nl", "adminPassword","dc=tsbd,dc=nl", false);
    }

}
