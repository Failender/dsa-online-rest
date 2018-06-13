package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class CacheHandler {


	private final File root;

	public CacheHandler(File root) {
		this.root = root;
	}

	public boolean hasCacheFor(ApiRequest<?> request) {
		return request.getCacheFile(root).exists();
	}

	public InputStream getCache(ApiRequest<?> request) {
		try {
			return new FileInputStream(request.getCacheFile(root));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void doCache(ApiRequest<?> request, InputStream is) {

		try {
			FileUtils.copyInputStreamToFile(is, request.getCacheFile(root));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
