package nl.tsbd.utils.odt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ODTReaderTest {

    private BiFunction<List<String>, String[], Regel> bi = (headers, values) -> new Regel(headers, values);

    @Test
    void zoekHeader() throws IOException {
        InputStream invoer = getClass().getResourceAsStream("invoer01.ods");
        ODTReader reader = new ODTReader(invoer, bi);

        List<String> headers = reader.getHeaders();

        assertThat(headers).hasSize(7);
        assertThat(headers.get(0)).isEqualTo("id");
        assertThat(headers.get(2)).isEqualTo("weergave naam");
        assertThat(headers.get(6)).isEqualTo("geslacht");
    }

    @Test
    void getWaardeUitRow() throws IOException {
        InputStream invoer = getClass().getResourceAsStream("invoer01.ods");
        ODTReader reader = new ODTReader(invoer, bi);

        List<Regel> rij = reader.getRegels();
        assertThat(rij).hasSize(2);
        assertThat(rij.get(1).findValue("id")).isEqualTo("370409");
        assertThat(rij.get(0).findValue("voornaam")).isEqualTo("Jan");
        assertThat(rij.get(0).findValue("geslacht")).isEqualTo("man");
    }
}
