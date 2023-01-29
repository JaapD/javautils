package nl.tsbd.util.properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PropertiesReaderTest {

    private static final String LDAP_PROPERTIES = "ldap-properties";

    enum PropertiesNames implements PropertiesNameEnum {
        LostPasswordUrl("lost.password.url"),
        LdapHost("ldaphost"),
        LdapPort("ldapport"),
        LdapRootElement("root"),
        AdminDN("admindn"),
        AdminPw("adminpw"),
        SmtpHost("smtp.host"),
        SmtpPort("smtp.port"),
        IsTls("is.tls");


        @Override
        public String getPropertyName() {
            return propertyName;
        }

        private final String propertyName;

        PropertiesNames(String propertyName) {
            this.propertyName = propertyName;
        }

    }

    private final PropertiesReader propertiesReader = new PropertiesReader() {
        @Override
        public  String getPropertyFilenameKey() {
            return LDAP_PROPERTIES;
        }
        @Override
        protected PropertiesNameEnum[] getPropertyValues() {
            return PropertiesNames.values();
        }

    };

    @Test
    void assert_that_exception_is_thrown__when_no_system_property_defined() {
        System.getProperties().remove(LDAP_PROPERTIES);
        assertThatThrownBy(propertiesReader::init)
                .hasMessageContaining("'ldap-properties' defined");
    }

    private void setSystemProperty(String s) {
        String name = getClass().getResource(s).getFile();
        System.setProperty(LDAP_PROPERTIES, name);
    }

    @Test
    void assert_that_missing_properies_throws_exception() {
        setSystemProperty("missing-properties.properties");
        assertThatThrownBy(propertiesReader::init)
                .hasMessageContaining("Missing properties")
                .hasMessageContaining("ldaphost");
    }

    @Test
    void assert_that_property_can_be_found() {
        setSystemProperty("correct-properties.properties");
        propertiesReader.init();
        assertThat(propertiesReader.get(PropertiesNames.LdapHost)).isEqualTo("localhost");
        assertThat(propertiesReader.getBoolean(PropertiesNames.LdapHost)).isFalse();
        assertThat(propertiesReader.getBoolean(PropertiesNames.IsTls)).isTrue();
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class LoggingTester {
        @Mock
        private Appender appender;
        @BeforeEach
        void startLog() {
            when(appender.getName()).thenReturn("mocking");
            when(appender.isStarted()).thenReturn(true);
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig("nl.tsbd.util.properties.PropertiesReader");
            loggerConfig.addAppender(appender, Level.WARN, null);
        }

        @AfterEach
        void tearDown() {
            LoggerContext context = LoggerContext.getContext(false);
            context.getConfiguration().removeLogger(appender.getName());
        }

        @Test
        void assert_that_to_much_properties_generate_warning() {
            String name = getClass().getResource("too-much-properties.properties").getFile();
            System.setProperty(LDAP_PROPERTIES, name);
            propertiesReader.init();
            ArgumentCaptor<LogEvent> event = ArgumentCaptor.forClass(LogEvent.class);
            verify(appender).append(event.capture());
            assertThat(event.getValue().getMessage().getFormattedMessage()).contains("not needed");
        }
    }

}
