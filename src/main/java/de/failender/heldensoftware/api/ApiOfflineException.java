package de.failender.heldensoftware.api;

public class ApiOfflineException extends RuntimeException {

	public ApiOfflineException() {
		super("Api is offline");
	}
}
