package de.failender.heldensoftware.api.authentication;

import java.util.Map;

public class TokenAuthentication implements Authentication {

	private final String token;


	public TokenAuthentication(String token) {
		if (token == null) {
			throw new IllegalArgumentException("Token can not be null");
		}
		this.token = token;
	}

	@Override
	public void writeToRequest(Map<String, String> data) {
		data.put("token", token);
	}

	public String getToken() {
		return token;
	}
}
