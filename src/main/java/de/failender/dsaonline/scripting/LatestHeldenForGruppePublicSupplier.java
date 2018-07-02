package de.failender.dsaonline.scripting;

import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LatestHeldenForGruppePublicSupplier extends ScriptSupplier<List<Daten>> {

	public static final String TYPE = "latestheldenforgruppepublic";

	@Autowired
	private HeldenService heldenService;

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public List<Daten> supply(String value) {

		return heldenService.findPublicByGruppeId(Integer.valueOf(value));
	}

	@Override
	public String rightNeeded(String value) {
		return null;
	}

	@Override
	public String description() {
		return "Holt die neusten Versionen aller öffentliche Helden einer Gruppe. Parameter ist die ID der Gruppe";
	}

	public void setHeldenService(HeldenService heldenService) {
		this.heldenService = heldenService;
	}
}
