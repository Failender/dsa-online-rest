package de.failender.dsaonline.restservice.helper;

import de.failender.heldensoftware.xml.datenxml.*;

import java.math.BigInteger;

public class DatenBuilder {

	private Daten daten = new Daten();

	{
		Ereignisse ereignisse = new Ereignisse();
		daten.setEreignisse(ereignisse);
		daten.setAngaben(new Angaben());

		daten.setTalentliste(new Talentliste());
		daten.setVorteile(new Vorteile());
		daten.setZauberliste(new Zauberliste());
		Eigenschaften eigenschaften = new Eigenschaften();
		daten.setEigenschaften(eigenschaften);

		mut(10);
		klugheit(10);
		intuition(10);
		charisma(10);
		fingerfertigkeit(10);
		gewandtheit(10);
		konsitution(10);
		koerperkraft(10);
	}

	public static DatenBuilder daten() {
		return new DatenBuilder();
	}

	public DatenBuilder addEreignis(Ereignis ereignis) {
		daten.getEreignisse().getEreignis().add(ereignis);
		return this;
	}

	public DatenBuilder addZauber(Zauber zauber) {
		daten.getZauberliste().getZauber().add(zauber);
		return this;
	}

	public DatenBuilder addTalente(Talent talent) {
		daten.getTalentliste().getTalent().add(talent);
		return this;
	}

	public DatenBuilder mut(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Mut");
		daten.getEigenschaften().setMut(wert);
		return this;
	}

	public DatenBuilder klugheit(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Klugheit");
		daten.getEigenschaften().setKlugheit(wert);
		return this;
	}

	public DatenBuilder intuition(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Intuition");
		daten.getEigenschaften().setIntuition(wert);
		return this;
	}

	public DatenBuilder charisma(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Charisma");
		daten.getEigenschaften().setCharisma(wert);
		return this;
	}

	public DatenBuilder fingerfertigkeit(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Fingerfertigkeit");
		daten.getEigenschaften().setFingerfertigkeit(wert);
		return this;
	}

	public DatenBuilder gewandtheit(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Gewandtheit");
		daten.getEigenschaften().setGewandtheit(wert);
		return this;
	}

	public DatenBuilder konsitution(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Konstitution");
		daten.getEigenschaften().setKonstitution(wert);
		return this;
	}

	public DatenBuilder koerperkraft(int value) {
		Eigenschaftswerte wert = new Eigenschaftswerte();
		wert.setAkt(BigInteger.valueOf(value));
		wert.setName("Körperkraft");
		daten.getEigenschaften().setKoerperkraft(wert);
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
