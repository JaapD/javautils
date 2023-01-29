package nl.tsbd.util.ldap;

import lombok.extern.slf4j.Slf4j;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.function.Function;

@Stateless
@Slf4j
public class LdapEjb {

    private final LdapPool ldapPool;

    public LdapEjb() {
        this(null);
    }

    @Inject
    public LdapEjb(LdapPool ldapPool) {
        this.ldapPool = ldapPool;
    }

    public <R> Object runWith(Function<LdapConnection, R> consumer) {
        LdapConnection connection = null;
        try {
            connection = ldapPool.getConnection();
            return consumer.apply(connection);
        } catch (LdapException e) {
            log.error("Error opening ldap connection ", e);
        } finally {
            if (connection != null) {
                try {
                    ldapPool.releaseConnection(connection);
                } catch (LdapException e) {
                    log.error("Error releasing connections", e);
                }
            }
        }
        return null;
    }
}
