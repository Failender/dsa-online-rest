package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

public class HeldenApi {

	private final CacheHandler cacheHandler;

	public HeldenApi(File cacheDirectory) {
		cacheHandler = new CacheHandler(cacheDirectory);
	}

	public <T> T request(ApiRequest<T> request, boolean useCache) {
		return request.mapResponse(requestRaw(request, useCache));
	}

	public InputStream requestRaw(ApiRequest<?> request, boolean useCache) {
		if (useCache) {
			if (cacheHandler.hasCacheFor(request)) {
				return cacheHandler.getCache(request);
			} else {
				InputStream is = doRequest(request);
				cacheHandler.doCache(request, is);
				return cacheHandler.getCache(request);
			}
		}
		return doRequest(request);
	}

	private InputStream doRequest(ApiRequest request) {
		Map<String, String> data = request.writeRequest();

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
