package de.failender.heldensoftware.xml.listtalente;

import javax.xml.bind.annotation.XmlElement;

public class SteigerungsTalent {

	@XmlElement(required = true)
	private String Talent;
	@XmlElement(required = true)
	private String Lernmethode;
	@XmlElement(required = true)
	private int Talentwert;
	@XmlElement(required = true)
	private String Art;
	@XmlElement(required = true)
	private int Kosten;


	public String getTalent() {
		return Talent;
	}

	public void setTalent(String talent) {
		Talent = talent;
	}

	public String getLernmethode() {
		return Lernmethode;
	}

	public void setLernmethode(String lernmethode) {
		Lernmethode = lernmethode;
	}

	public int getTalentwert() {
		return Talentwert;
	}

	public void setTalentwert(int talentwert) {
		Talentwert = talentwert;
	}

	public String getArt() {
		return Art;
	}

	public void setArt(String art) {
		Art = art;
	}

	public int getKosten() {
		return Kosten;
	}

	public void setKosten(int kosten) {
		Kosten = kosten;
	}
}
