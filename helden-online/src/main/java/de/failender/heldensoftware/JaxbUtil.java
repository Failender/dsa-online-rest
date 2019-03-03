package de.failender.heldensoftware;

import de.failender.heldensoftware.api.CorruptXmlException;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class JaxbUtil {

	public static Daten datenFromFile(File file) {
		try {
			InputStream is = new FileInputStream(file);
			Unmarshaller unmarshaller = getUnmarshaller(Daten.class);
			Daten daten = (Daten) unmarshaller.unmarshal(is);
			return daten;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	public static List<Held> heldenFromFile(File file) {
		try {
			InputStream is = new FileInputStream(file);

			Unmarshaller unmarshaller = getUnmarshaller(Helden.class);
			Helden daten = (Helden) unmarshaller.unmarshal(is);
			return daten.getHeld();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}
	public static Marshaller getMarshaller(Class... classes) {
		try {
			JAXBContext ctx = JAXBContext.newInstance(classes);
			return ctx.createMarshaller();
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	public static Unmarshaller getUnmarshaller(Class... classes) {
		try {
			JAXBContext ctx = JAXBContext.newInstance(classes);
			return ctx.createUnmarshaller();
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	public static Daten datenFromStream(InputStream is) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Daten.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Daten daten = (Daten) jaxbUnmarshaller.unmarshal(is);
			return daten;
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		} catch (Exception e) {
			throw new ExchangeException(e);
		}

	}

	public static List<Held> heldenFromStream(InputStream is) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Helden.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Helden helden = (Helden) jaxbUnmarshaller.unmarshal(is);
			return helden.getHeld();
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		} catch (Exception e) {
			throw new ExchangeException(e);
		}

	}
}
