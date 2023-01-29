package nl.tsbd.util.ldap;

import lombok.extern.log4j.Log4j2;
import nl.tsbd.util.ldap.exception.ModifyLdapException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Log4j2
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LdapEjbTest {

    @Mock
    private LdapPool ldapPool;
    private LdapEjb ejb;

    @BeforeEach
    void setUp() throws ModifyLdapException {
        ejb = new LdapEjb(ldapPool);
    }

    @Test
    void when_runWith_throws_exception__null_is_returned() throws LdapException {
        when(ldapPool.getConnection()).thenThrow(new LdapException("error"));
        Object result = ejb.runWith((c) -> "hi");
        assertThat(result).isNull();
    }
}
