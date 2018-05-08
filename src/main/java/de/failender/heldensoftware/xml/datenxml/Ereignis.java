package de.failender.heldensoftware.xml.datenxml;

import javax.xml.bind.annotation.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ereignis")
@XmlType(name = "", propOrder = {
		"aktion", "alterzustand", "ap", "asp", "bemerkung", "date", "kp", "kommentar", "lep", "neuerzustand", "object", "version"
})
public class Ereignis {

	@XmlElement(required = true)
	private String aktion;
	@XmlElement
	private Integer alterzustand;
	@XmlElement
	private int ap;
	@XmlElement
	private int asp;
	@XmlElement
	private String bemerkung;
	@XmlElement
	private String date;
	@XmlElement
	private int kp;
	@XmlElement
	private String kommentar;
	@XmlElement
	private int lep;
	@XmlElement
	private Integer neuerzustand;
	@XmlElement
	private String object;
	@XmlElement
	private String version;

	public String getAktion() {
		return aktion;
	}

	public void setAktion(String aktion) {
		this.aktion = aktion;
	}

	public Integer getAlterzustand() {
		return alterzustand;
	}

	public void setAlterzustand(Integer alterzustand) {
		this.alterzustand = alterzustand;
	}

	public int getAp() {
		return ap;
	}

	public void setAp(int ap) {
		this.ap = ap;
	}

	public int getAsp() {
		return asp;
	}

	public void setAsp(int asp) {
		this.asp = asp;
	}

	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}

	private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy kk:mm");

	public Long getDate() {
		try {
			return format.parse(date).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getKp() {
		return kp;
	}

	public void setKp(int kp) {
		this.kp = kp;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}

	public int getLep() {
		return lep;
	}

	public void setLep(int lep) {
		this.lep = lep;
	}

	public Integer getNeuerzustand() {
		return neuerzustand;
	}

	public void setNeuerzustand(Integer neuerzustand) {
		this.neuerzustand = neuerzustand;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
