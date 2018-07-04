package de.failender.dsaonline.scripting.supplier;

import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.SelectData;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HeldByIdLatestSupplier extends ScriptSupplier<Daten> {

	public static final String TYPE = "heldbyidlatest";

	@Autowired
	private HeldenService heldenService;

	@Autowired
	private HeldRepository heldRepository;

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public Daten supply(String value) {
		BigInteger id = new BigInteger(value);
		Daten daten = heldenService.findHeldWithLatestVersion(id);

		return daten;
	}

	@Override
	public String rightNeeded(String value) {
		return SecurityUtils.VIEW_ALL;
	}

	@Override
	public String description() {
		return "Gibt die neuste Version des Helden mit der gegebenen ID zurück";
	}

	@Override
	public List<SelectData> getPossibleValues() {
		return heldRepository.findAll()
				.stream()
				.map(value -> new SelectData(value.getName(), value.getId()))
				.collect(Collectors.toList());
	}
}
