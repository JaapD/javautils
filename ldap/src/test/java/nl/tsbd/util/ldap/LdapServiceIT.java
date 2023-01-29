package nl.tsbd.util.ldap;

import lombok.extern.slf4j.Slf4j;
import nl.tsbd.util.ldap.exception.LoginFailedException;
import nl.tsbd.util.ldap.exception.NotFoundInLdapException;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LdapServiceIT extends LdapIntegrationTest {

    private LdapService service;

    @BeforeEach
    void setUp() {
        LdapPool ldapPool = new LdapPool(ldapProperties);
        ldapPool.init();
        LdapEjb ldapEjb = new LdapEjb(ldapPool);
        service = new LdapService(ldapEjb, ldapProperties);
    }

    @Test
    void when_find_person__then_return_person_with_all_fields() throws LoginFailedException, NotFoundInLdapException, LdapInvalidAttributeValueException {
        Entry found = service.findPerson("marie");
        Attribute mail = found.get("mail");
        log.info("found {}, class: {} ", mail, found.getClass());
        assertThat(mail.getString()).isEqualTo("ldapmodify@localhost");
        assertThat(found.get("uid").getString()).isEqualTo("marie");
    }

    @Test
    void find_groups_for_dn_finds_all_groups() throws NotFoundInLdapException, LdapInvalidAttributeValueException {
        Entry found = service.findPerson("marie");
        List<Entry> groups = service.findGroups(found.getDn());
        assertThat(groups).hasSize(3);
        assertThat(groups).anyMatch(e -> e.getDn().toString().equals("cn=visitor,ou=groups,dc=test,dc=nl"));
    }
    @Test
    void find_groups_for_dn_finds_also_group_in_group() throws NotFoundInLdapException, LdapInvalidAttributeValueException {
        Entry found = service.findPerson("Marie");
        List<Entry> groups = service.findGroups(found.getDn());
        assertThat(groups).hasSize(3);
        assertThat(groups).anyMatch(e -> e.getDn().toString().contains("member"));
        assertThat(groups).anyMatch(e -> e.getDn().toString().contains("ingroup"));
    }
}
