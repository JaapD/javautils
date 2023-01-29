package nl.tsbd.utils.odt;


import lombok.extern.slf4j.Slf4j;
import nl.tsbd.utils.odt.intern.LibreOfficeNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ODTReader {

    private static final LibreOfficeNamespaceContext NAMESPACE_CONTEXT = new LibreOfficeNamespaceContext();
    private final BiFunction<List<String>, String[], Regel> ctor;
    private List<String> headers;
    private List<Regel> regels;

    public ODTReader(InputStream invoer, BiFunction<List<String>, String[], Regel> ctor) throws IOException {
        this.ctor = ctor;

        ZipInputStream in = new ZipInputStream(invoer);
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            log.debug("Bezig met: " + entry.getName());
            if (entry.getName().equals("content.xml")) {
                byte[] b = new byte[1024];
                int gelezen;
                StringBuilder sb = new StringBuilder();
                while ((gelezen = in.read(b)) > 0) {
                    sb.append(new String(b, 0, gelezen, Charset.forName("utf-8")));
                }
                verwerkXML(sb.toString());
                break;
            }
        }

    }

    private void verwerkXML(String in) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream bin = new ByteArrayInputStream(in.getBytes("utf-8"));
            Document document = builder.parse(bin);
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(NAMESPACE_CONTEXT);
            //NodeList nl = (NodeList) xPath.evaluate("/office:document-content/office:body/office:spreasheet/table:table/table:table-row", document, XPathConstants.NODESET);
            NodeList nl = (NodeList) xPath.evaluate("/office:document-content/office:body/office:spreadsheet/table:table/table:table-row", document, XPathConstants.NODESET);
            if (nl.getLength() > 0) {
                createHeaders(nl.item(0));
            }
            regels = new ArrayList<>();
            for (int i = 1; i < nl.getLength(); i++) {
                regels.add(ctor.apply(headers, leesRowIn(nl.item(i)).toArray(new String[0])));
            }
            log.debug("Aantal regels gevonden: " + nl.getLength());
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            log.error("Het ging mis", e);
            throw new RuntimeException(e);
        }

    }

    private void createHeaders(Node item) throws XPathExpressionException {
        headers = leesRowIn(item);
    }

    private List<String> leesRowIn(Node item) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(NAMESPACE_CONTEXT);
        NodeList nl = (NodeList) xPath.evaluate("table:table-cell", item, XPathConstants.NODESET);
        List<String> inhoud = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {

            Node attr = (Node) xPath.evaluate("@table:number-columns-repeated", nl.item(i), XPathConstants.NODE);
            log.debug(i + " ->a " + attr);
            Node text = (Node) xPath.evaluate("text:p", nl.item(i), XPathConstants.NODE);
            int nr = 1;
            if (attr != null) {
                nr = Integer.parseInt(attr.getTextContent());
            }
            for (int j = 0; j < nr; j++) {
                String str = text == null ? "" : text.getTextContent();
                inhoud.add(str);
                log.debug(i + " -> " + str);
            }
        }
        return inhoud;
    }


    public List<String> getHeaders() {
        return headers;
    }

    public List<Regel> getRegels() {
        return regels;
    }
}
