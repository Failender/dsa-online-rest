package de.failender.heldensoftware.api;

import de.failender.heldensoftware.FailedRequestsRetrier;
import de.failender.heldensoftware.api.requests.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class HeldenApi {

	private final CacheHandler cacheHandler;
	private final FailedRequestsRetrier failedRequestsRetrier;

	public HeldenApi(File cacheDirectory) {
		cacheHandler = new CacheHandler(cacheDirectory);
		failedRequestsRetrier = new FailedRequestsRetrier(this);
		System.out.println("test");
	}

	public <T> Optional<T> request(ApiRequest<T> request) {
		Future f;
		
		return request(request, true);
	}

	public <T> T requestOrThrow(ApiRequest<T> request, boolean useCache) {
		return request(request, useCache).orElseThrow(() -> new ApiOfflineException());
	}

	public <T> Optional<T> request(ApiRequest<T> request, boolean useCache) {
		Optional<InputStream> is = (requestRaw(request, useCache));
		if(is == null) {
			return Optional.empty();
		}
		return is.map(stream -> request.mapResponse(stream));
	}

	public void provideDownload(ApiRequest<?> request, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		try {
			Optional<InputStream> inputStreamOptional = requestRaw(request, true);
			IOUtils.copy(inputStreamOptional.orElseThrow(() -> new ApiOfflineException()), response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream requestRawOrThrow(ApiRequest request, boolean useCache) {
		return requestRaw(request, useCache).orElseThrow(() -> new ApiOfflineException());
	}

 	public Optional<InputStream> requestRaw(ApiRequest request, boolean useCache) {
		if (useCache) {
			if (cacheHandler.hasCacheFor(request)) {
				return Optional.of(cacheHandler.getCache(request));
			}
		}
		InputStream is = doRequest(request);
		if(is == null) {
			return null;
		}
		if(cacheHandler.canCache(request)) {
			cacheHandler.doCache(request, is);
			return Optional.of(cacheHandler.getCache(request));
		}
		return Optional.of(is);


	}

	private synchronized InputStream doRequest(ApiRequest request) {
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
			log.error("Critical error while performing request to url {} with body {}", request.url(), body);
			failedRequestsRetrier.addFailedRequest(request);
			return null;
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
		pdfintern, heldenxml, datenxml
	}

	public CacheHandler getCacheHandler() {
		return cacheHandler;
	}
}
