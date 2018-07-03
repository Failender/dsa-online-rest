package de.failender.dsaonline.scripting.supplier;

import java.util.List;

public abstract class ScriptSupplier<T> {
	public abstract String type();
	public abstract T supply(String value);
	public abstract String rightNeeded(String value);
	public abstract String description();
	public abstract List<String> getPossibleValues();
}
