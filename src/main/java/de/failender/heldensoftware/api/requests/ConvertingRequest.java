package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConvertingRequest extends ApiRequest<InputStream> {

	private final HeldenApi.Format format;
	private final String xml;

	public ConvertingRequest(HeldenApi.Format format, String xml) {
		this.format = format;
		this.xml = xml;

	}

	@Override
	public String url() {
		return super.url() + "/converter/?format=" + format;
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		data.put("held", "xml");

		return data;
	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return null;
	}

	@Override
	public File getCacheFile(File root) {
		return null;
	}
}