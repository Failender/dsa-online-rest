package de.failender.dsaonline.api;

import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;

import java.math.BigInteger;
import java.util.List;

public interface HeldenSoftwareAPI {

	Daten getHeldenDaten(BigInteger heldenid);
	String getHeldXml(BigInteger heldenid);
	List<Held> getAllHelden();



}
