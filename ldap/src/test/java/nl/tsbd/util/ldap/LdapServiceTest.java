package nl.tsbd.util.ldap;

import nl.tsbd.util.ldap.exception.LoginFailedException;
import nl.tsbd.util.ldap.exception.ModifyPasswordException;
import nl.tsbd.util.ldap.exception.NotFoundInLdapException;
import nl.tsbd.util.ldap.exception.NotFoundInLdapRuntimeException;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LdapServiceTest extends LdapIntegrationTest {
    @Mock
    private LdapEjb ldapEjb;

    private LdapService service;

    @BeforeEach
    void setUp() {
        service = new LdapService(ldapEjb, ldapProperties);
    }

    @Test
    void when_login__then_return_person() throws LoginFailedException, LdapInvalidDnException {
        Entry person = new DefaultEntry("abc=a");
        when(ldapEjb.runWith(any())).thenReturn(person);

        Entry found = service.login("test", "password");

        assertThat(found.getDn().toString()).isEqualTo("abc=a");
    }

    @Test
    void when_login_with_empty_name_or_password__then_exception_is_thrown() throws LoginFailedException {
        assertThatThrownBy(() -> service.login("", "password"))
                .isExactlyInstanceOf(LoginFailedException.class)
                .hasMessageContaining("fout");
        assertThatThrownBy(() -> service.login("uid", ""))
                .isExactlyInstanceOf(LoginFailedException.class)
                .hasMessageContaining("fout");
    }

    @Test
    void when_password_change__then_bean_is_called() throws ModifyPasswordException {
        service.modifyPassword("name", "oldpassword", "newpassword");

        verify(ldapEjb).runWith(any());
    }

    @Test
    void when_uid_providec__then_person_is_returned() throws NotFoundInLdapException {
        service.findPerson("any");
        verify(ldapEjb).runWith(any());
    }

    @Test
    void when_find_person_is_not_found__then_exception_is_thrown() throws NotFoundInLdapException {
        when(ldapEjb.runWith(any())).thenThrow(new NotFoundInLdapRuntimeException("error"));
        assertThatThrownBy(() -> service.findPerson("any"))
                .hasMessageContaining("foute login")
                .isExactlyInstanceOf(NotFoundInLdapException.class);
    }
}
