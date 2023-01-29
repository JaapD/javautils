package nl.boerendroogers.utils.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Geef de configuratie uit een bepaalde omgeving. Dit kan zowel uit een JNDI als uit config files
 * in de /nl.boerendroogers.utils.conf/ folder. <br/>
 * De configuratiefile met het hoogste "prioriteit" nummer wordt ingelezen, maar JNDI gaat voor.
 *
 * @author jan
 */
@Slf4j
public class Configurator {

    private static final String[] RESOURCEFILES = new String[]{"conf/dbconfigtst.xml", "conf/dbconfig.xml", "conf/config.xml"};

    /**
     * De configuration waar alle waardes in zitten.
     */
    private Configuration configuration;

    /**
     * De enkele instance van de Configurator.
     */
    private static final Configurator ikzelf = new Configurator();

    /**
     * Initialiseer de configurator met de initConfigNaam. Die mag null zijn, dan wordt er gewoon op
     * het classpath gezocht. Als de initialisatie al gedaan is, gaat ie nogmaals aan de gang.
     *
     * @param initConfigNaam De naam waar in de initialcontext gezocht moet worden naar de
     *                       configfile.
     */
    public void init(String initConfigNaam) {
        configuration = null;
        if (initConfigNaam != null) {
            zoekConfigContext(initConfigNaam);
        }
        if (configuration == null) {
            zoekConfig();
        }
    }

    private Configurator() {
        try {
            zoekConfig();
        } catch (Exception e) {
            log.debug("Fout bij zoekConfig in configurator, mogelijk is dat geen probleem." + e.getMessage());
        }
    }

    /**
     * Geef de configurator zelf terug.
     *
     * @return
     */
    public static Configurator getConfigurator() {
        return ikzelf;
    }

    /**
     * Geef een veld van een bepaalde naam. Als het niet gevonden is, komt er null terug.
     *
     * @param veldNaam
     * @return De waarde van het veld of null als het niet gevonden is.
     */
    public static String getVeld(String veldNaam) {
        return ikzelf.configuration.getString(veldNaam);
    }

    /**
     * Doorzoek alle configfiles. <br>
     * <e>Post</e> Alle velden zijn gezet.
     */
    private void zoekConfig() {
        log.info("Zoeken van database in config-files");
        try {
            List<URL> configs = zoekConfigs();
            int oudeprio = -1;
            for (Iterator<URL> i = configs.iterator(); i.hasNext(); ) {
                try {
                    URL url = i.next();
                    log.debug("Inlezen configuratie: " + url);
                    Configuration config = propertiesBuilder(url.getFile());
                    int prio = config.getInt("prioriteit");
                    if (prio > oudeprio) {
                        configuration = config;
                        log.debug("Prio was hoger: " + prio);
                        oudeprio = prio;
                    }
                } catch (ConfigurationException e) {
                    log.error("Fout bij configuratie", e);
                }
            }
        } catch (IOException e) {
            log.error("Probleem met resources");
            throw new RuntimeException("Probleem met resources", e);
        }
    }

    /**
     * Zoek alle configfiles die te vinden zijn.
     *
     * @return
     * @throws IOException
     */
    private List<URL> zoekConfigs() throws IOException {
        List<URL> result = new ArrayList<URL>();
        log.info("zoeken naar configuratiefiles");
        for (String s : RESOURCEFILES) {
            Enumeration<URL> configs = getClass().getClassLoader().getResources(s);
            while (configs.hasMoreElements()) {
                URL element = configs.nextElement();
                result.add(element);
                log.info("{}", element);
            }
        }
        if (result.isEmpty()) {
            throw new RuntimeException("Er zijn geen configuratiefiles gevonden");
        }
        return result;
    }

    /**
     * Zoek de configfile in de InitialContext. <br>
     * <e>Post</e> configuratie is gezet, als die gevonden is, anders blijft ie null.
     *
     * @param initConfigNaam De naam van de config, mag niet null zijn.
     * @return De naam van de configfile uit de context, of null als die er niet stond..
     */
    private void zoekConfigContext(String initConfigNaam) {
        try {
            log.debug("Zoeken in de context");
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            String configFile = (String) envCtx.lookup(initConfigNaam);
            log.debug("configfile is " + configFile);
            configuration = propertiesBuilder(configFile);
        } catch (NamingException e) {
            log.info("Naming exception, initial context niet gevonden " + e.getMessage());
        } catch (ConfigurationException e) {
            log.error("Fout bij openen configfile uit initial context" + e);
            throw new RuntimeException("Fout bij openen configfile uit initial context", e);
        }
    }

    /**
     * Geef een veld als boolean terug.
     *
     * @param veldNaam
     * @return
     */
    public static boolean getVeldBoolean(String veldNaam) {
        return Boolean.valueOf(getVeld(veldNaam));
    }

    private XMLConfiguration propertiesBuilder(String file) throws ConfigurationException {
        FileBasedConfigurationBuilder<XMLConfiguration> builder =
                new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFileName(file));

        return builder.getConfiguration();
    }
}
