package nl.boerendroogers.utils.conf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguratorTest {

    
    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testGetVeldLeeg() throws Exception {
        String gevonden = Configurator.getVeld("iserniet");
        assertNull(gevonden);
    }

    @Test
    void testGetVeldGevuld() throws Exception {
        String gevonden = Configurator.getVeld("DB.User");
        assertNotNull(gevonden);
        assertEquals("SA", gevonden);
    }

    @Test
    void testGetVeldBoolean() throws Exception {
        boolean gevonden = Configurator.getVeldBoolean("inmemory");
        assertTrue(gevonden);
        gevonden = Configurator.getVeldBoolean("Is er niet ");
        assertFalse(gevonden);
    }

    @Test
    void testGetVeldMetPunt() throws Exception {
        String gevonden = Configurator.getVeld("DB.User");
        assertNotNull(gevonden);
        assertEquals("SA", gevonden);
    }

}
