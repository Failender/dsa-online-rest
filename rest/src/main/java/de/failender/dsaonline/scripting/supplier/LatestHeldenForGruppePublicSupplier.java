package de.failender.dsaonline.scripting.supplier;

import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.SelectData;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LatestHeldenForGruppePublicSupplier extends ScriptSupplier<List<Daten>> {

	public static final String TYPE = "latestheldenforgruppepublic";

	@Autowired
	private HeldenService heldenService;

	@Autowired
	private GruppeRepository gruppeRepository;

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

	@Override
	public List<SelectData> getPossibleValues() {
		return gruppeRepository.findAll()
				.stream()
				.map(val -> new SelectData(val.getName(), val.getId()))
				.collect(Collectors.toList());
	}
}
