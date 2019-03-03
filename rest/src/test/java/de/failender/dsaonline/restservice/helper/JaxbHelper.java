package de.failender.dsaonline.restservice.helper;

import de.failender.heldensoftware.JaxbUtil;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class JaxbHelper {

	public static InputStream marshall(Object o) throws JAXBException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JaxbUtil.getMarshaller(o.getClass()).marshal(o, bos);
		return new ByteArrayInputStream(bos.toByteArray());

	}
}
