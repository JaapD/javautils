package nl.tsbd.util.ldap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.AbstractPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.MonitoringLdapConnection;
import org.apache.directory.ldap.client.api.ValidatingPoolableLdapConnectionFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Slf4j
@Singleton
public class LdapPool {

    private final LdapProperties ldapProperties;
    private LdapConnectionPool pool;

    public LdapPool() {
        this(null);
        // Necessary for Weld
    }

    @Inject
    public LdapPool(LdapProperties ldapProperties) {
        this.ldapProperties = ldapProperties;
    }

    public LdapConnection getConnection() throws LdapException {
        MonitoringLdapConnection connection = (MonitoringLdapConnection) pool.getConnection();
        try {
            connection.bind();
        }catch (LdapException e) {
            log.warn("Error calling bind: {}", e.getMessage());
            log.trace("Error calling bind ", e);
            releaseConnection(connection);
            pool.getConnection();
        }
        log.debug("Get connection {}", connection);
        return connection;
    }

    public void releaseConnection(LdapConnection connection) throws LdapException {
        pool.releaseConnection(connection);
    }

    @PostConstruct
    public void init() {
        log.info("Create new connection pool");
        LdapConnectionConfig config = createLdapConnectionConfig();
        AbstractPoolableLdapConnectionFactory factory = createLdapConnectionFactory(config);
        GenericObjectPoolConfig<LdapPool> poolConfig = createPoolConfig();
        pool = new LdapConnectionPool(factory, poolConfig);
    }


    private AbstractPoolableLdapConnectionFactory createLdapConnectionFactory(LdapConnectionConfig config) {
        ValidatingPoolableLdapConnectionFactory factory = new ValidatingPoolableLdapConnectionFactory(config);
        return factory;
    }

    private LdapConnectionConfig createLdapConnectionConfig() {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(ldapProperties.ldapHost);
        config.setLdapPort(ldapProperties.ldapPort);
        config.setName(ldapProperties.adminDn);
        config.setCredentials(ldapProperties.adminPw );
        config.setUseTls(ldapProperties.isTls);
        return config;
    }

    private GenericObjectPoolConfig<LdapPool> createPoolConfig() {
        GenericObjectPoolConfig<LdapPool> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setLifo(true);
        poolConfig.setMaxTotal(4);
        poolConfig.setMaxIdle(4);
        poolConfig.setMinEvictableIdleTimeMillis(1000L * 60L * 30L);
        poolConfig.setMinIdle(0);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setSoftMinEvictableIdleTimeMillis(-1L);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setTimeBetweenEvictionRunsMillis(-1L);
        poolConfig.setBlockWhenExhausted(false);
        return poolConfig;
    }
}
