package de.failender.dsaonline.scripting.helper;

import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class DatenHelper extends ScriptHelper {
	@Override
	public String getName() {
		return "datenHelper";
	}


	@DocumentedMethod(description = "Extrahiert den Namen aus den Daten", returnDescription = "Der Name")
	public static String name(@DocumentedParameter(name = "daten", description = "Die Daten des Helden") Daten daten) {
		return daten.getAngaben().getName();
	}
	@DocumentedMethod(description = "Extrahiert die Gesamt-AP aus den Daten", returnDescription = "Die Gesamt-AP")
	public static BigInteger ap(@DocumentedParameter(name = "daten", description = "Die Daten des Helden") Daten daten) {
		return daten.getAngaben().getAp().getGesamt();
	}
	@DocumentedMethod(description = "Extrahiert die Freien AP aus den Daten", returnDescription = "Die Freien-AP")
	public static BigInteger apFrei(@DocumentedParameter(name = "daten", description = "Die Daten des Helden") Daten daten) {
		return daten.getAngaben().getAp().getFrei();
	}


}
