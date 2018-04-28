package de.failender.dsaonline.heldensoftware;

import de.failender.dsaonline.heldensoftware.exception.CorruptXmlException;
import de.failender.dsaonline.heldensoftware.exception.ExchangeException;
import de.failender.dsaonline.heldensoftware.xml.datenxml.Daten;
import de.failender.dsaonline.heldensoftware.xml.datenxml.ObjectFactory;
import de.failender.dsaonline.heldensoftware.xml.datenxml.Talent;
import de.failender.dsaonline.heldensoftware.xml.heldenliste.Held;
import de.failender.dsaonline.heldensoftware.xml.heldenliste.Helden;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class HeldenSoftwareAPI {

	private String token;
	public HeldenSoftwareAPI(String token) throws Exception {
		this.token = token;
		List<Held> helden = getAllHelden();
		helden.forEach(held -> {
			Daten daten = getHeldenDaten(held.getHeldenid());
			daten.getTalentliste().getTalent().forEach(talent -> {
				System.out.println(held.getName() + ' ' + talent.getNameausfuehrlich());
			});
		});

	}

	private List<Held> getAllHelden() {


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

	private Daten getHeldenDaten(BigInteger heldenid) {

		// Helden anfordern

		try {
			InputStreamReader stringheld = Helper.postrequeststream("action", "returnheld",
					"format", "datenxml",
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



	public static void main(String[] args) throws Exception {

		new HeldenSoftwareAPI("cead7ff39138dfb94171f19d8b46a487a4f1f53ad120ce819d6c0d86787b8c65");

	}
}
