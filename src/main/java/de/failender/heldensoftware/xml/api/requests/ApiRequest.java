package de.failender.heldensoftware.xml.api.requests;

import jdk.internal.util.xml.impl.Input;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class ApiRequest<T> {

	public abstract String url();

	public abstract void writeToRequest(Map<String, String> data);

	public abstract T mapResponse(InputStream is);
}
