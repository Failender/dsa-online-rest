package de.failender.dsaonline.scripting.supplier;

import de.failender.dsaonline.data.entity.abenteuer.AbenteuerEntity;
import de.failender.dsaonline.data.repository.AbenteuerRepository;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.rest.dto.AbenteuerDto;
import de.failender.dsaonline.service.AbenteuerService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.SelectData;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LatestAbenteuerForGruppeSupplier extends ScriptSupplier<AbenteuerDto> {

	public static final String TYPE = "latestabenteuerforgruppe";

	@Autowired
	private AbenteuerRepository abenteuerRepository;

	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private AbenteuerService abenteuerService;

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public AbenteuerDto supply(String value) {
		return abenteuerService.toDto(abenteuerRepository.findTopOneByGruppeIdOrderByDatumDesc(Integer.valueOf(value)));
	}

	@Override
	public String rightNeeded(String value) {
		return null;
	}

	@Override
	public String description() {
		return "Holt die neusten Versionen aller öffentliche Helden einer Gruppe. Parameter ist die ID der Gruppe";
	}

	@Override
	public List<SelectData> getPossibleValues() {
		return gruppeRepository.findAll()
				.stream()
				.map(val -> new SelectData(val.getName(), val.getId()))
				.collect(Collectors.toList());
	}
}
