package de.failender.heldensoftware.xml.api.authentication;

import java.util.Map;

public class TokenAuthentication implements Authentication{

	private final String token;

	public TokenAuthentication(String token) {
		this.token = token;
	}

	@Override
	public void writeToRequest(Map<String, String> data) {
		data.put("token", token);
	}
}
