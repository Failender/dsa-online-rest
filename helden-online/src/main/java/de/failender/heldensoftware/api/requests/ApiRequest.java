package de.failender.heldensoftware.api.requests;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public abstract class ApiRequest<T> {

	public String url() {
		return "https://online.helden-software.de";
	}

	public abstract Map<String, String> writeRequest();

	public abstract T mapResponse(InputStream is);

	public abstract File getCacheFile(File root);

	public String requestMethod() {
		return "POST";
	}
}
