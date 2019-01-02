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

		File file = request.getCacheFile(root);
		return file != null && file.exists();
	}

	public InputStream getCache(ApiRequest<?> request) {
		try {
			return new FileInputStream(request.getCacheFile(root));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean canCache(ApiRequest<?> apiRequest) {
		return apiRequest.getCacheFile(root) != null;
	}

	public void doCache(ApiRequest<?> request, InputStream is) {

		try {
			File cacheFile = request.getCacheFile(root);
			if(cacheFile != null) {
				FileUtils.copyInputStreamToFile(is, cacheFile);
			}
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeCache(ApiRequest request) {
		File cacheFile = request.getCacheFile(root);
		if(cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	public File getRoot() {
		return root;
	}
}
