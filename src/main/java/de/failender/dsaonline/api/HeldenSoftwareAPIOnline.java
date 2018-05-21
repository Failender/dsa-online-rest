package de.failender.dsaonline.api;

import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.exceptions.ExchangeException;
import de.failender.heldensoftware.Helper;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;



@Slf4j
public class HeldenSoftwareAPIOnline implements HeldenSoftwareAPI {

	private final Environment env;
	private String token;
	public HeldenSoftwareAPIOnline(String token, Environment env){
		this.env = env;
		this.token = token;

	}

	public List<Held> getAllHelden() {
		log.info("Fetching all helden for " + token);
		try {

			InputStreamReader is = Helper.postrequeststream("action", "listhelden",
					"token", token);
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
		log.info("Getting helden daten for " + heldenid);
		try {

			InputStreamReader stringheld = Helper.postrequeststream("action", "returnheld",
					"format", "datenxml",
					"opt","ereignisse",
					"heldenid", heldenid.toString(),
					"token", token);
			JAXBContext jaxbContext = JAXBContext.newInstance(Daten.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Daten daten = (Daten) jaxbUnmarshaller.unmarshal(stringheld);
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
			return Helper.postrequest("action", "returnheld",
					"format", "datenxml",
					"heldenid", heldenid.toString(),
					"opt","ereignisse",
					"token", token);
		} catch (IOException e) {
			throw new ExchangeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExchangeException(e);
		}
	}
}
