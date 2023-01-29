package nl.tsbd.util.ldap;

import lombok.extern.log4j.Log4j2;
import nl.tsbd.util.ldap.exception.ModifyLdapException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Log4j2
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LdapEjbIT extends LdapIntegrationTest {

    private LdapEjb ejb;

    @BeforeEach
    void setUp() throws ModifyLdapException {
        LdapPool ldapPool = new LdapPool(ldapProperties);
        ldapPool.init();
        ejb = new LdapEjb(ldapPool);
    }

    @Test
    void when_runWith_is_called_often__same_connection_is_used() throws LdapException {
        AtomicInteger hashCode = new AtomicInteger();
        AtomicInteger o = (AtomicInteger) ejb.runWith((c) -> {
            hashCode.set(c.hashCode());
            return hashCode;
        });
        log.info("Result is {} ", o);
        for (int i = 0; i < 10; i++) {
            ejb.runWith((c) -> assertThat(c.hashCode()).isEqualTo(hashCode.get()));
        }
    }

}
