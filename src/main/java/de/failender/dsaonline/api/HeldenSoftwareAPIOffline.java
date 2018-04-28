package de.failender.dsaonline.api;

import de.failender.dsaonline.heldensoftware.exception.CorruptXmlException;
import de.failender.dsaonline.heldensoftware.exception.ExchangeException;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;


@Service
@ConditionalOnProperty(value = "dsa.heldensoftware.online", havingValue = "false")
public class HeldenSoftwareAPIOffline implements HeldenSoftwareAPI {

	public List<Held> getAllHelden() {


		try {
			InputStream is =HeldenSoftwareAPIOffline.class.getClassLoader().getResourceAsStream("api/offline/helden/response.xml");

			JAXBContext jaxbContext = JAXBContext
					.newInstance(Helden.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Helden helden = (Helden) jaxbUnmarshaller.unmarshal(is);
			return helden.getHeld();
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		} catch (Exception e) {
			throw new ExchangeException(e);
		}
	}

	public Daten getHeldenDaten(BigInteger heldenid) {

		try {

			InputStream is =HeldenSoftwareAPIOffline.class.getClassLoader().getResourceAsStream("api/offline/helden/"+heldenid.toString()+".xml");

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

	@Override
	public String getHeldXml(BigInteger heldenid) {
		try {
			return IOUtils.toString(HeldenSoftwareAPIOffline.class.getClassLoader().getResourceAsStream("api/offline/helden/"+heldenid.toString()+".xml"));
		} catch (IOException e) {
			throw new ExchangeException(e);
		}
	}
}
