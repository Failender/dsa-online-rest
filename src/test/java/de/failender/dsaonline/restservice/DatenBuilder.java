package de.failender.dsaonline.restservice;

import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import de.failender.heldensoftware.xml.datenxml.Ereignisse;

public class DatenBuilder {

	private Daten daten = new Daten();

	{
		Ereignisse ereignisse = new Ereignisse();
		daten.setEreignisse(ereignisse);
	}

	public static DatenBuilder builder() {
		return new DatenBuilder();
	}

	public DatenBuilder addEreignis(Ereignis ereignis) {
		daten.getEreignisse().getEreignis().add(ereignis);
		return this;
	}

	public Daten build() {
		return daten;
	}
}
