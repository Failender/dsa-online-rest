package de.failender.dsaonline.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

public class XmlUtil {

	public static Document documentFromString(String xml) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Document createDocument(){
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toString(Document document) throws TransformerException {
		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.transform(new DOMSource(document), new StreamResult(sw));
		return sw.toString();
	}

	public static Date getStandFromString(String xml) {
		Node node = XmlUtil.documentFromString(xml).getDocumentElement().getFirstChild();
		while(!(node instanceof Element)) {
			node = node.getNextSibling();
		}
		String stand = ((Element) node).getAttribute("stand");

		Date date = new Date(Long.valueOf(stand));
		return date;
	}
}
