package de.failender.dsaonline.restservice.helper;

import de.failender.dsaonline.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;

public class HeldXmlBuilder {

	private Document document;

	private Element held;
	public HeldXmlBuilder() {
		document = XmlUtil.createDocument();
		Element helden = document.createElement("helden");
		document.appendChild(helden);
		held = document.createElement("held");
		helden.appendChild(held);
	}

	public HeldXmlBuilder stand(Long stand) {
		held.setAttribute("stand", stand + "");
		return this;
	}

	public HeldXmlBuilder key(Long key) {
		held.setAttribute("key", key+ "");
		return this;
	}

	public byte[] build() throws TransformerException, UnsupportedEncodingException {
		return XmlUtil.toString(document).getBytes("UTF-8");
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		XMLUtils.outputDOM(document, bos, true);
//		return bos.toByteArray();
	}

	public String asString() throws TransformerException {
		return XmlUtil.toString(document);
	}

	public static HeldXmlBuilder heldxml() {
		return new HeldXmlBuilder();
	}
}
