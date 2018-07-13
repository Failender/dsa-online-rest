package de.failender.dsaonline.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Date;

public class XmlUtil {

	public static Document documentFromString(String xml) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Date getStandFromString(String xml) {
		String stand = ((Element)(XmlUtil.documentFromString(xml).getDocumentElement().getFirstChild())).getAttribute("stand");
		Date date = new Date(Long.valueOf(stand));
		return date;
	}
}
