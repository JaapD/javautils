package nl.tsbd.utils.auth;

import lombok.extern.slf4j.Slf4j;
import nl.tsbd.util.ldap.LdapService;
import nl.tsbd.util.ldap.exception.LoginFailedException;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class AuthenticationService {
    private final LdapService ldapService;
    private final String shouldHaveGroup;
    private final String adminGroup;


    public AuthenticationService() {
        ldapService = null;
        shouldHaveGroup = null;
        adminGroup = null;
    }

    @Inject
    public AuthenticationService(LdapService ldapService, AuthenticationProperties authenticationProperties) {
        this.ldapService = ldapService;
        shouldHaveGroup = authenticationProperties.get(AuthenticationProperties.PropertyNames.ShouldHaveGroup);
        adminGroup = authenticationProperties.get(AuthenticationProperties.PropertyNames.AdminGroup);
    }

    public User getUser(String loginName, String password) throws LoginFailedException {
        try {
            Entry login = ldapService.login(loginName, password);
            if (login == null) {
                log.warn("Invalid login for '{}'", loginName);
                throw new LoginFailedException("username or password invalid");
            }
            User user = getUserWithGroups(login);
            validateUserHasRoles(user);
            return user;
        } catch (LdapInvalidAttributeValueException e) {
            log.info("Error getting user: {} {}", e.getClass().getName(), e.getMessage());
            throw new LoginFailedException("username or password invalid");
        }
    }

    private void validateUserHasRoles(User user) throws LoginFailedException {
        if (!user.hasRoles()) {
            log.warn("No groups for '{}'", user.getName());
            throw new LoginFailedException("username or password invalid");
        }
    }

    private User getUserWithGroups(Entry login) throws LdapInvalidAttributeValueException {
        User user = new User(login.get("uid").getString());

        List<Entry> groups = ldapService.findGroups(login.getDn());
        for (Entry g : groups) {
            if (g.getDn().getName().equalsIgnoreCase(shouldHaveGroup)) {
                user.addRole(new Role("user"));
            } else if (g.getDn().getName().equalsIgnoreCase(adminGroup)) {
                user.addRole(new Role("admin"));
                user.setAdmin(true);
            }
        }
        return user;
    }
}
