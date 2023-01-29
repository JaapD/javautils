package nl.tsbd.util.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * An abstract reader for properties.
 * When instanncing this class, implement the two abstract methods and don't forget to call the init() method!
 */
@Slf4j
public abstract class PropertiesReader {

    private static final String NO_SYSTEM_PROPERTY_LDAP_PROPERTIES_DEFINED = "No system property '%s' defined";

    private Properties properties;

    public void init() {
        log.info("Initialisation properties reader");
        String propertieFileName = System.getProperty(getPropertyFilenameKey());
        if (propertieFileName == null) {
            log.error(getNoSystemPropertyLdapPropertiesDefined());
            throw new IllegalArgumentException(getNoSystemPropertyLdapPropertiesDefined());
        }
        properties = new Properties();
        try {
            properties.load(new FileInputStream(propertieFileName));
            verifyUnneededProperties(propertieFileName);
            verifyAllPropertiesExist(propertieFileName);
        } catch (IOException e) {
            log.error("Error reading properties from " + propertieFileName, e);
        }
    }

    private String getNoSystemPropertyLdapPropertiesDefined() {
        return String.format(NO_SYSTEM_PROPERTY_LDAP_PROPERTIES_DEFINED, getPropertyFilenameKey());
    }

    public abstract String getPropertyFilenameKey();

    /**
     * Mak use of an enum that implements the interface PropertiesNameEnum.
     * @return YourEnum.values();
     */
    protected abstract PropertiesNameEnum[] getPropertyValues();

    private void verifyUnneededProperties(String propertieFileName) {
        for (String key : properties.stringPropertyNames()) {
            boolean exist = false;
            for (PropertiesNameEnum prop : getPropertyValues()) {
                if (key.equals(prop.getPropertyName())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                log.warn("Property not needed in file: {}, {}", propertieFileName, key);
            }
        }
    }

    private void verifyAllPropertiesExist(String propertieFileName) {
        StringBuilder missing = new StringBuilder();
        for (PropertiesNameEnum prop : getPropertyValues()) {
            if (get(prop) == null) {
                missing.append(prop.getPropertyName()).append(" ");
            }
        }
        if (missing.length() > 0) {
            throw new PropertyReaderException(
                    MessageFormat.format("Missing properties in file: {0}, {1}", propertieFileName, missing));
        }
    }

    public String get(PropertiesNameEnum property) {
        return properties.getProperty(property.getPropertyName());
    }

    public boolean getBoolean(PropertiesNameEnum property) {
        return get(property).equalsIgnoreCase("true");
    }
}
