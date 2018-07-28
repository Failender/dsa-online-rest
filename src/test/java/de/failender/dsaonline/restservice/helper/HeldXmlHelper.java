package de.failender.dsaonline.restservice.helper;

import de.failender.dsaonline.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;

public class HeldXmlHelper {

	private Document document;

	private Element held;
	public HeldXmlHelper() {
		document = XmlUtil.createDocument();
		Element helden = document.createElement("helden");
		document.appendChild(helden);
		held = document.createElement("held");
		helden.appendChild(held);
	}

	public HeldXmlHelper stand(Long stand) {
		held.setAttribute("stand", stand + "");
		return this;
	}

	public byte[] build() throws TransformerException, UnsupportedEncodingException {
		return XmlUtil.toString(document).getBytes("UTF-8");
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		XMLUtils.outputDOM(document, bos, true);
//		return bos.toByteArray();
	}

	public static HeldXmlHelper heldxml() {
		return new HeldXmlHelper();
	}
}
