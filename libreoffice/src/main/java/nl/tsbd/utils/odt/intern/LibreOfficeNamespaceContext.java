package nl.tsbd.utils.odt.intern;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LibreOfficeNamespaceContext implements NamespaceContext {

    private static final Map<String, String> NS = createNamespaces();

    @Override
    public String getNamespaceURI(String prefix) {
        return NS.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : NS.entrySet()) {
            if (namespaceURI.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }

    private static Map<String, String> createNamespaces() {
        HashMap<String, String> ns = new HashMap<>();
        ns.put("table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
        ns.put("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
        ns.put("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
        return ns;
    }
}
