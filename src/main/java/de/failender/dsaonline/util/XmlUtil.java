package de.failender.dsaonline.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.function.Predicate;

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

	public static String toString(Document document) {
		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();

		try {
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty("standalone", "yes");
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(new DOMSource(document), new StreamResult(sw));
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Date getStandFromString(String xml) {

		String stand = getHeldFromXml(xml).getAttribute("stand");

		Date date = new Date(Long.valueOf(stand));
		return date;
	}
	public static long getKeyFromString(String xml) {

		String stand = getHeldFromXml(xml).getAttribute("key");
		return Long.parseLong(stand);
	}


	public static Element getHeldFromXml(String xml) {
		Node node = XmlUtil.documentFromString(xml).getDocumentElement().getFirstChild();
		while(!(node instanceof Element)) {
			node = node.getNextSibling();
		}
		return (Element) node;
	}

	public static Element findIn(NodeList nodeList, Predicate<Element> predicate) {
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(!(node instanceof Element)) {
				continue;
			}
			Element element = (Element) node;
			if(predicate.test(element)) {
				return element;
			}
		}
		return null;
	}
}
