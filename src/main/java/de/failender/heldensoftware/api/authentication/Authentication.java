package de.failender.heldensoftware.api.authentication;

import java.util.Map;

public interface Authentication {
	void writeToRequest(Map<String, String> data);
}
