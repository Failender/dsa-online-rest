package de.failender.heldensoftware.xml.datenxml;


import javax.xml.bind.annotation.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ereignis")
@XmlType(name = "", propOrder = {
		"aktion", "alterzustand", "ap", "asp", "bemerkung", "date", "kp", "kommentar", "lep", "neuerzustand", "object", "version"
})
public class Ereignis {

	@XmlElement(required = true)
	private String aktion;
	@XmlElement
	private String alterzustand;
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
	private String neuerzustand;
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

	public String getAlterzustand() {
		return alterzustand;
	}

	public void setAlterzustand(String alterzustand) {
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

	public String getNeuerzustand() {
		return neuerzustand;
	}

	public void setNeuerzustand(String neuerzustand) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Ereignis)) return false;
		Ereignis ereignis = (Ereignis) o;
		return ap == ereignis.ap &&
				asp == ereignis.asp &&
				kp == ereignis.kp &&
				lep == ereignis.lep &&
				Objects.equals(aktion, ereignis.aktion) &&
				Objects.equals(alterzustand, ereignis.alterzustand) &&
				Objects.equals(bemerkung, ereignis.bemerkung) &&
				Objects.equals(date, ereignis.date) &&
				Objects.equals(kommentar, ereignis.kommentar) &&
				Objects.equals(neuerzustand, ereignis.neuerzustand) &&
				Objects.equals(object, ereignis.object) &&
				Objects.equals(version, ereignis.version);
	}

	@Override
	public int hashCode() {

		return Objects.hash(aktion, alterzustand, ap, asp, bemerkung, date, kp, kommentar, lep, neuerzustand, object, version);
	}

	@Override
	public String toString() {
		return "Ereignis{" +
				"aktion='" + aktion + '\'' +
				", alterzustand='" + alterzustand + '\'' +
				", ap=" + ap +
				", asp=" + asp +
				", bemerkung='" + bemerkung + '\'' +
				", date='" + date + '\'' +
				", kp=" + kp +
				", kommentar='" + kommentar + '\'' +
				", lep=" + lep +
				", neuerzustand='" + neuerzustand + '\'' +
				", object='" + object + '\'' +
				", version='" + version + '\'' +
				'}';
	}
}
