package nl.tsbd.utils.auth;

import nl.tsbd.util.ldap.LdapService;
import nl.tsbd.util.ldap.exception.LoginFailedException;
import nl.tsbd.util.testutils.ExtendWithMockito;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWithMockito
class AuthenticationServiceTest {
    private AuthenticationService service;
    @Mock
    private LdapService ldapService;
    @Mock
    private AuthenticationProperties authenticationProperties;

    @BeforeEach
    void setUp() {
        when(authenticationProperties.get(eq(AuthenticationProperties.PropertyNames.ShouldHaveGroup))).thenReturn("uid=role1,ou=groups,dc=test,dc=nl");
        service = new AuthenticationService(ldapService, authenticationProperties);
    }

    @Test
    void testMethods() throws LoginFailedException, LdapInvalidAttributeValueException {
        mockEntry();
        mockRoles();

        User u = service.getUser("uid", "password");

        assertThat(u.getName()).isEqualTo("Username");
        assertThat(u.hasRole("role1")).isFalse();
        assertThat(u.hasRole("user")).isTrue();
    }

    private void mockEntry() throws LdapInvalidAttributeValueException, LoginFailedException {
        Entry entry = Mockito.mock(Entry.class);
        Attribute attribute = Mockito.mock(Attribute.class);
        when(attribute.getString()).thenReturn("Username");
        when(entry.get("uid")).thenReturn(attribute);
        when(ldapService.login(any(), any())).thenReturn(entry);
    }

    private void mockRoles() {
        Entry entry = Mockito.mock(Entry.class);
        Dn dn = mock(Dn.class);
        when(dn.getName()).thenReturn("uid=role1,ou=groups,dc=test,dc=nl");
        when(entry.getDn()).thenReturn(dn);
        List<Entry> groups = new ArrayList<>();
        groups.add(entry);
        when(ldapService.findGroups(any())).thenReturn(groups);
    }
}
