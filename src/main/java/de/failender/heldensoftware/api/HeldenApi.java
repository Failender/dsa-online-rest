package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HeldenApi {

	private final CacheHandler cacheHandler;
	private final FailedRequestsRetrier failedRequestsRetrier;
	private final HttpClient httpClient;

	public HeldenApi(File cacheDirectory) {
		this.httpClient = HttpClients.custom()
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
		cacheHandler = new CacheHandler(cacheDirectory);
		failedRequestsRetrier = new FailedRequestsRetrier(this);
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
			Mono.just(cacheHandler.getCache(request));
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

	private Mono<InputStream> doRequest(ApiRequest request) {

		HttpPost httpPost = new HttpPost(request.url());
		Map<String, String> data = request.writeRequest();
		String body = buildBody(data);
		try {
			httpPost.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		httpPost.setHeader("Content-Length", String.valueOf(body.length()));
		return Mono.create(consumer -> {

			try {
				HttpResponse response = httpClient.execute(httpPost);
				consumer.success(response.getEntity().getContent());
			} catch (IOException e) {
				log.info("############");
				log.info("Received failed requests to url ", request.url());
				log.info("Body is: ");
				log.info(body);
				log.info("############");
				e.printStackTrace();
				throw new ApiOfflineException();
			}

		});

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
