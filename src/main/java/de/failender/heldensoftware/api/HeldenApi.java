package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

	private Mono<InputStream> doRequest(ApiRequest request) {

		HttpPost httpPost = new HttpPost(request.url());
		Map<String, String> data = request.writeRequest();
		String body = buildBody(data);
		try {
			httpPost.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		HttpEntity<String> entity = new HttpEntity<>(body, header);
		Mono<InputStream> mono = Mono.fromCallable(() -> {

			try {
				ResponseEntity<Resource> response = null;
				response = restTemplate.exchange(request.url(), HttpMethod.POST, entity, Resource.class);

				if(response.getStatusCode().value() == 503) {
					logError(request, body);
					throw new ApiOfflineException();

				} else {
					return response.getBody().getInputStream();
				}

			} catch (IOException e) {
				logError(request, body);
				e.printStackTrace();
				throw new ApiOfflineException();

			} catch(HttpServerErrorException e) {
				logError(request, body);
				e.printStackTrace();
				log.error("Error from api {}: {}",e.getStatusCode(), e.getStatusText());
				throw new ApiOfflineException();
			}
		});

		return mono
				.subscribeOn(Schedulers.elastic());

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
