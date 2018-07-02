package de.failender.dsaonline.scripting;

public abstract class ScriptSupplier<T> {
	public abstract String type();
	public abstract T supply(String value);
	public abstract String rightNeeded(String value);
	public abstract String description();
}
