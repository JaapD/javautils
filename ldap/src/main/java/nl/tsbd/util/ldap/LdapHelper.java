package nl.tsbd.util.ldap;

import lombok.extern.slf4j.Slf4j;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;

@Slf4j
public class LdapHelper {
    public static String getString(Entry entry, String attributeKey) {
        try {
            Attribute values = entry.get(attributeKey);
            if (values == null) {
                return null;
            }
            return values.getString();
        } catch (LdapInvalidAttributeValueException e) {
            log.info("Invalid attribute '{}' for {}", attributeKey, entry);
            log.trace("Invalid attribute: ", e);
            return null;
        }
    }
}
