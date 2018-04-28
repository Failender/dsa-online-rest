package de.failender.dsaonline.api;

import de.failender.dsaonline.heldensoftware.exception.CorruptXmlException;
import de.failender.dsaonline.heldensoftware.exception.ExchangeException;
import de.failender.heldensoftware.Helper;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;


@Service
@ConditionalOnProperty("dsa.heldensoftware.online")
public class HeldenSoftwareAPIOnline implements HeldenSoftwareAPI {

	private String token;
	public HeldenSoftwareAPIOnline(@Value("${dsa.heldensoftware.token}") String token){
		this.token = token;

	}

	public List<Held> getAllHelden() {

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

		HeldenSoftwareAPIOnline api  =new HeldenSoftwareAPIOnline("cead7ff39138dfb94171f19d8b46a487a4f1f53ad120ce819d6c0d86787b8c65");
		api.getAllHelden().forEach(held -> {
			api.getHeldenDaten(held.getHeldenid());
		});

	}
}
