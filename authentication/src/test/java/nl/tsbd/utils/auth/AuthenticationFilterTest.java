package nl.tsbd.utils.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationFilterTest {

    AuthenticationFilter filter;

    @BeforeEach
    void setUp() throws IOException {
        try (InputStream properties = getClass().getResourceAsStream("auth.properties")) {
            PropertiesForAuthentication propertiesForAuthentication = new PropertiesForAuthentication();
            propertiesForAuthentication.load(properties);
            filter = new AuthenticationFilter(propertiesForAuthentication);
            filter.init();
        }
    }

    @Test
    void testDefaultUrlsAreProceedable() throws IOException {
        assertThat(filter.canProceed("/index.jsf")).isTrue();
        assertThat(filter.canProceed("/logout.jsf")).isTrue();
    }

    @Test
    void testGeneralUrlsAreProceedable() {
        assertThat(filter.canProceed("/javax.faces.resource/more")).isTrue();
    }

    @Test
    void testForbiddenUrls() {
        assertThat(filter.canProceed("forbidden")).isFalse();
        assertThat(filter.canProceed("/WrongStart/javax.faces.resource/more")).isFalse();
    }
}
