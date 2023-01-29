package nl.tsbd.utils.odt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Een regel uit een spreadsheet. Waarde van een veld kan uit de header gehaald worden.
 */
public class Regel {

    private final List<String> header;
    private final String[] inhoud;

    /**
     * Maak een nieuwe regel.
     *
     * @param header Een list met headers met namen.
     * @param inhoud De inhoud van de regel.
     */
    public Regel(List<String> header, String... inhoud) {
        this.header = header;
        this.inhoud = inhoud;
    }

    /**
     * Vindt een waarde van een bepaalde kolom.
     *
     * @param kolomnaam De kolomnaam.
     * @return De waarde in de cel onder die kolom.
     * @throws IllegalArgumentException als de kolom niet bestaat.
     */
    public String findValue(String kolomnaam) {
        int nr = header.indexOf(kolomnaam);
        if (nr == -1 | nr >= inhoud.length) {
            throw new IllegalArgumentException("Kolom '" + kolomnaam + "' niet gevonden");
        }
        return inhoud[nr];
    }

    /**
     * Geef een map met alle velden voor deze regel.
     * @return Map met 0 of meer velden.
     */
    public Map<String, String> getVeldenMap() {
        Map<String, String> result = new HashMap<>();
        for (String h:header) {
            result.put(h, findValue(h));
        }
        return result;
    }
}
