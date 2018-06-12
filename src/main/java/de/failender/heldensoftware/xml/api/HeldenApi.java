package de.failender.heldensoftware.xml.api;

import de.failender.heldensoftware.xml.api.authentication.Authentication;
import de.failender.heldensoftware.xml.api.requests.ApiRequest;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HeldenApi {

	public InputStream request(ApiRequest request, Authentication authentication) {
		Map<String, String> data = new HashMap<>();
		request.writeToRequest(data);
		authentication.writeToRequest(data);
		String body = buildBody(data);
		try {
			URL url = new URL(request.url());

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(body);
			writer.close();

			return connection.getInputStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private String buildBody(Map<String, String> data) {
		return data.entrySet()
				.stream()
				.map(this::mapEntry)
				.collect(Collectors.joining("&"));
	}

	private String mapEntry(Map.Entry<String, String> entry) {
		try {
			return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Format {
		pdfinternal, heldenxml, datenxml
	}
}
