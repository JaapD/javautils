package nl.tsbd.util.ldap;

import lombok.extern.slf4j.Slf4j;
import nl.tsbd.util.ldap.exception.LdapSearchException;
import nl.tsbd.util.ldap.exception.LoginFailedException;
import nl.tsbd.util.ldap.exception.LoginFailedRuntimeException;
import nl.tsbd.util.ldap.exception.ModifyLdapException;
import nl.tsbd.util.ldap.exception.ModifyPasswordException;
import nl.tsbd.util.ldap.exception.NotFoundInLdapException;
import nl.tsbd.util.ldap.exception.NotFoundInLdapRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Slf4j
public class LdapService implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LdapEjb ldapEjb;
    private final String rootElement;

    public LdapService() {
        // necessary for Weld
        ldapEjb = null;
        rootElement = null;
    }

    @Inject
    public LdapService(LdapEjb ldapEjb, LdapProperties ldapProperties) {
        this.ldapEjb = ldapEjb;
        rootElement = ldapProperties.root;
    }

    public Entry login(String uid, String password) throws LoginFailedException {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(password)) {
            throw new LoginFailedException("foute login");
        }
        try {
            return (Entry) ldapEjb.runWith((c) -> login(c, uid, password));
        } catch (LdapSearchException | LoginFailedRuntimeException e) {
            log.info("Foute login {}", e.getMessage());
            throw new LoginFailedException("foute login");
        }
    }

    public void modifyPassword(String uid, String oldPassword, String newPassword) throws ModifyPasswordException {
        try {
            ldapEjb.runWith((c) -> modifyPassword(c, uid, oldPassword, newPassword));
        } catch (ModifyLdapException e) {
            log.info("Er ging ergens iets mis {}", e.getMessage());
            throw new ModifyPasswordException("er ging ergens iets mis " + e.getMessage());
        }
    }

    public void modifyPasswordWithoutOldPassword(String uid, String newPassword) throws ModifyPasswordException {
        try {
            ldapEjb.runWith((c) -> modifyPasswordWithoutOldPassword(c, uid, newPassword));
        } catch (ModifyLdapException e) {
            log.info("Er ging ergens iets mis {}", e.getMessage());
            throw new ModifyPasswordException("er ging ergens iets mis " + e.getMessage());
        }
    }

    public Entry findPerson(String uid) throws NotFoundInLdapException {
        try {
            return (Entry) ldapEjb.runWith((c) -> findPerson(c, uid));
        } catch (NotFoundInLdapRuntimeException lfe) {
            log.info("Person not found {}", lfe.getMessage());
            throw new NotFoundInLdapException("foute login");
        }
    }

    private Entry findPerson(LdapConnection connection, String uid) {
        try {
            return getAttributesForUid(connection, uid);
        } catch (LdapException | CursorException e) {
            log.info("Error finding person {}: {} ", uid, e.getClass());
            throw new NotFoundInLdapRuntimeException("Error login");
        }
    }

    private Entry login(LdapConnection connection, String uid, String password) {
        try {
            Entry attributes = getAttributesForUid(connection, uid);
            Dn dn = attributes.getDn();
            connection.bind(dn, password);
            return attributes;
        } catch (LdapException | CursorException e) {
            log.info("Error login {}: {} ", uid, e.getClass());
            throw new LoginFailedRuntimeException("Error login");
        }
    }

    private Void modifyPassword(LdapConnection connection, String uid, String oldPassword, String newPassword) {
        try {
            Entry attributes = getAttributesForUid(connection, uid);
            Dn dn = attributes.getDn();
            connection.bind(dn, oldPassword);
            modifyPasswordForDN(connection, newPassword, dn);
            return null;
        } catch (LdapAuthenticationException e) {
            log.info("Wrong authentication for ''{}'' ", uid);
            throw new LoginFailedRuntimeException(MessageFormat.format("Loging failed for ''{0}''", uid));
        } catch (LdapException | CursorException e) {
            log.info("Error change password for ''{}'' : ''{}'' ", uid, e.getMessage());
            throw new ModifyLdapException(MessageFormat.format("Error to change password for uid ''{0}''", uid));
        }
    }

    private Void modifyPasswordWithoutOldPassword(LdapConnection connection, String uid, String newPassword) {
        try {
            Entry attributes = getAttributesForUid(connection, uid);
            Dn dn = attributes.getDn();
            modifyPasswordForDN(connection, newPassword, dn);
            return null;
        } catch (LdapException | CursorException e) {
            log.info("Error change password for ''{}'' : ''{}'' ", uid, e.getMessage());
            throw new ModifyLdapException(MessageFormat.format("Error to change password for uid  ''{0}''", uid));
        }
    }

    private void modifyPasswordForDN(LdapConnection connection, String newPassword, Dn dn) throws LdapException {
        String crypt = SecurityUtil.crypt(newPassword);
        Modification replaceGn = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "userPassword",
                crypt);
        connection.modify(dn, replaceGn);
    }

    private Entry getAttributesForUid(LdapConnection connection, String uid) throws LdapException, CursorException {
        Entry attributes;
        EntryCursor cursor = connection.search(rootElement, "(uid=" + uid + ")", SearchScope.SUBTREE);
        if (cursor.next()) {
            attributes = cursor.get();
            log.debug("found: {}", attributes.getDn());
        } else {
            String warning = MessageFormat.format("Nothing found for {0}", uid);
            log.info(warning);
            throw new LdapSearchException(warning);
        }
        if (cursor.next()) {
            throw new LdapSearchException("Multiple entries found for " + uid);
        }
        return attributes;
    }

    public List<Entry> findGroups(Dn dn) {
        return (List<Entry>) ldapEjb.runWith((c) -> findGroups(c, dn));
    }

    private List<Entry> findGroups(LdapConnection connection, Dn dn) {
        log.debug("Searching for groups for: {}", dn.toString());
        try {
            Map<Dn, Entry> result = new HashMap<>();
            queryGroupsForDn(connection, dn, result);
            log.debug("Found {} groups", result.size());
            return new ArrayList<>(result.values());
        } catch (LdapException | CursorException e) {
            throw new LdapSearchException("Problem searching for groups:", e);
        }
    }

    private void queryGroupsForDn(LdapConnection connection, Dn dn, Map<Dn, Entry> resultToAddGroups) throws LdapException, CursorException {
        String queryString = String.format("(&(objectClass=groupOfNames)(member=%s))", dn.toString());
        EntryCursor cursor = connection.search(rootElement, queryString, SearchScope.SUBTREE);
        while (cursor.next()) {
            Entry entry = cursor.get();
            log.trace("found group: {}", entry.getDn());
            if (!resultToAddGroups.containsKey(entry.getDn())) {
                resultToAddGroups.put(entry.getDn(), entry);
                queryGroupsForDn(connection, entry.getDn(), resultToAddGroups);
            }
        }
    }
}
