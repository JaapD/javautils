package nl.tsbd.util.ldap;

import lombok.extern.log4j.Log4j2;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@Log4j2
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LdapPoolIT extends LdapIntegrationTest {

    private LdapPool pool;

    @BeforeEach
    void setUp() {
        pool = new LdapPool(ldapProperties);
        pool.init();
    }

    @Test
    void when_asked_connection__then_users_can_be_found() throws LdapException, CursorException {
        LdapConnection connection = pool.getConnection();
        connection.bind("uid=jan,ou=users,dc=test,dc=nl", "secret");
        connection.bind();
        EntryCursor cursor = connection.search("dc=test,dc=nl", "(objectclass=*)", SearchScope.SUBTREE, "*");
        boolean found = false;
        while (cursor.next()) {
            found = true;
            Entry entry = cursor.get();
            log.info("Entry is: {}", entry);
        }
        assertThat(found).isTrue();
    }

    @Test
    void when_asked_and_released_many_connection__then_same_connection_is_returned() throws LdapException, IOException {
        LdapConnection connection = pool.getConnection();
        for (int i = 0; i < 5; i++) {
            pool.releaseConnection(connection);
            LdapConnection nextConnection = pool.getConnection();
            assertThat(nextConnection).isSameAs(connection);
            connection = nextConnection;
        }
        pool.releaseConnection(connection);
    }

    @Test
    void when_asked_too_many_connections__then_exception_is_thrown() throws LdapException {
        assertThatThrownBy(() -> {
            for (int i = 0; i < 5; i++) {
                pool.getConnection();
            }
        })
                .isExactlyInstanceOf(NoSuchElementException.class)
                .hasMessage("Pool exhausted");
    }

}
