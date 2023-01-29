package nl.tsbd.utils.odt;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegelTest {

    @Test
    void testHeaders() {
        List<String> header = Arrays.asList("e-mail", "voornaam", "Inzetbaar bij evenementen", "omschrijving");
        String[] inhoud = { "van.de@server.computer", "van", "1", "een verhaal" };
        Regel regel = new Regel(header, inhoud);
        assertThat(regel.findValue("e-mail")).isEqualTo("van.de@server.computer");
        assertThat(regel.findValue("voornaam")).isEqualTo("van");
    }

    @Test
    void testFouteHeader() {
        List<String> header = Arrays.asList("emailx", "voornaam", "Inzetbaar bij evenementen", "omschrijving");
        String[] inhoud = { "van.de@server.computer", "van", "1", "een verhaal" };
        Regel regel = new Regel(header, inhoud);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> regel.findValue("iserniet"));
        assertThat(exception.getMessage()).contains("iserniet");
    }

    @Test
    void testMapVanvelden() {
        List<String> header = Arrays.asList("e-mail", "voornaam", "Inzetbaar bij evenementen", "omschrijving");
        String[] inhoud = { "van.de@server.computer", "van", "1", "een verhaal" };
        Regel regel = new Regel(header, inhoud);

        Map<String, String> velden = regel.getVeldenMap();

        assertThat(velden).hasSize(4);
        assertThat(velden.get("e-mail")).isEqualTo("van.de@server.computer");
        assertThat(velden.get("voornaam")).isEqualTo("van");

    }
}
