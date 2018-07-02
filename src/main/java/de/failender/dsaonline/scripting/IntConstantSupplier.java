package de.failender.dsaonline.scripting;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class IntConstantSupplier extends ScriptSupplier {

	public static final String TYPE = "intconstant";



	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public Integer supply(String value) {
		return Integer.valueOf(value);
	}

	@Override
	public String rightNeeded(String value) {
		return null;
	}

	@Override
	public String description() {
		return "Gibt den angegebenen Wert zurück (als Int)";
	}

	@Override
	public List<String> getPossibleValues() {
		return Collections.emptyList();
	}
}
