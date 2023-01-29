package nl.tsbd.utils.auth;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Dependent
public class PropertiesProvider {
    private static final String PROPERTIES = "/WEB-INF/auth.properties";
    private PropertiesForAuthentication properties;

    @Inject
    ServletContext context;

    @Produces
    PropertiesForAuthentication getAuthenticationProperties() {
        return properties;
    }

    @PostConstruct
    public void init() {
        log.info("Read the properties");
        try (InputStream configStream = context.getResourceAsStream(PROPERTIES)) {
            if (configStream == null) {
                log.error("Unable to read properties from {} ", PROPERTIES);
            } else {
                properties = new PropertiesForAuthentication();
                properties.load(configStream);
            }
        } catch (IOException e) {
            log.error("Unable to read properties", e);
        }
    }
}
