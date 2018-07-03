package de.failender.dsaonline.scripting.supplier;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConstantSupplier extends ScriptSupplier {

	public static final String TYPE = "constant";



	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public String supply(String value) {
		return value;
	}

	@Override
	public String rightNeeded(String value) {
		return null;
	}

	@Override
	public String description() {
		return "Gibt den angegebenen Wert zurück (als String)";
	}

	@Override
	public List<String> getPossibleValues() {
		return null;
	}
}
