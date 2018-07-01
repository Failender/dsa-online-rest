package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HeldenApi {

	private final CacheHandler cacheHandler;
	private final RestTemplate restTemplate;

	public HeldenApi(File cacheDirectory, RestTemplate restTemplate) {

		this.restTemplate = restTemplate;
		cacheHandler = new CacheHandler(cacheDirectory);
	}

	public <T> Mono<T> request(ApiRequest<T> request) {
		return request(request, true);
	}

	public <T> Mono<T> request(ApiRequest<T> request, boolean useCache) {
		return requestRaw(request, useCache)
				.map(is -> request.mapResponse(is));
	}

	public void provideDownload(ApiRequest<?> request, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		requestRaw(request, true)
				.subscribe((is) -> {
					try {
						IOUtils.copy(is, response.getOutputStream());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});

	}

	public Mono<InputStream> requestRaw(ApiRequest request, boolean useCache) {
		if (useCache && cacheHandler.hasCacheFor(request)) {
			return Mono.just(cacheHandler.getCache(request));
		}
		return doRequest(request)
				.map(is -> {
					if (cacheHandler.canCache(request)) {
						cacheHandler.doCache(request, is);
						return cacheHandler.getCache(request);
					} else {
						return is;
					}
				});
	}

	private synchronized Mono<InputStream> doRequest(ApiRequest request) {
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

			return Mono.just(connection.getInputStream());
		} catch (Exception e) {
			logError(request, body);
			throw new RuntimeException(e);
		}

	}

	private void logError(ApiRequest request, String body) {
		log.info("############");
		log.info("Received failed request to url {}", request.url());
		log.info("Body is: ");
		log.info(body);
		log.info("############");
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
		pdfintern, heldenxml, datenxml
	}

	public CacheHandler getCacheHandler() {
		return cacheHandler;
	}


}
