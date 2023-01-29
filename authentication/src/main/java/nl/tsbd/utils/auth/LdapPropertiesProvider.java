package nl.tsbd.utils.auth;

import lombok.extern.slf4j.Slf4j;
import nl.tsbd.util.ldap.LdapProperties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Slf4j
@Dependent
public class LdapPropertiesProvider {
    private final LdapProperties properties;

    @Inject
    public LdapPropertiesProvider(AuthenticationProperties ldapProperties) {
        log.debug("loaded new ldapProperties");
        properties = new LdapProperties(
                ldapProperties.get(AuthenticationProperties.PropertyNames.LdapHost),
                Integer.parseInt(ldapProperties.get(AuthenticationProperties.PropertyNames.LdapPort)),
                ldapProperties.get(AuthenticationProperties.PropertyNames.LdapAdminDn),
                ldapProperties.get(AuthenticationProperties.PropertyNames.LdapAdminPw),
                ldapProperties.get(AuthenticationProperties.PropertyNames.LdapRoot),
                ldapProperties.getBoolean(AuthenticationProperties.PropertyNames.LdapTls)
        );
    }

    @Produces
    public LdapProperties getProperties() {
        return properties;
    }

}
