package de.failender.dsaonline.restservice.helper;

import de.failender.heldensoftware.xml.datenxml.*;

import java.math.BigInteger;

public class DatenBuilder {

	private Daten daten = new Daten();

	{
		Ereignisse ereignisse = new Ereignisse();
		daten.setEreignisse(ereignisse);
		daten.setAngaben(new Angaben());
	}

	public static DatenBuilder builder() {
		return new DatenBuilder();
	}

	public DatenBuilder addEreignis(Ereignis ereignis) {
		daten.getEreignisse().getEreignis().add(ereignis);
		return this;
	}

	public DatenBuilder apGesamt(Long ap) {
		if(daten.getAngaben().getAp() == null) {
			daten.getAngaben().setAp(new Ap());
		}
		daten.getAngaben().getAp().setGesamt(BigInteger.valueOf(ap));
		return this;
	}

	public Daten build() {
		return daten;
	}
}
