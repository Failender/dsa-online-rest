package de.failender.heldensoftware.api;

import de.failender.heldensoftware.api.requests.ApiRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldPdfRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class Helper {

	public static File getFileFor(ApiRequest<?> request, CacheHandler cacheHandler) {
		return request.getCacheFile(cacheHandler.getRoot());
	}

	public static void copyFilesToHigherVersion(BigInteger heldid, int version, CacheHandler cacheHandler) {
		copyPdfToHigherVersion(heldid, version, cacheHandler);
		copyXmlToHigherVersion(heldid, version, cacheHandler);
		copyDatenToHigherVersion(heldid, version, cacheHandler);
	}

	public static void copyPdfToHigherVersion(BigInteger heldid, int version, CacheHandler cacheHandler) {
		File from = getFileFor(new ReturnHeldPdfRequest(heldid, null, version), cacheHandler);
		File to = getFileFor(new ReturnHeldPdfRequest(heldid, null, version + 1), cacheHandler);
		try {
			FileUtils.moveFile(from, to);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyXmlToHigherVersion(BigInteger heldid, int version, CacheHandler cacheHandler) {
		File from = getFileFor(new ReturnHeldXmlRequest(heldid, null, version), cacheHandler);
		File to = getFileFor(new ReturnHeldXmlRequest(heldid, null, version + 1), cacheHandler);
		try {
			FileUtils.moveFile(from, to);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyDatenToHigherVersion(BigInteger heldid, int version, CacheHandler cacheHandler) {
		File from = getFileFor(new ReturnHeldDatenWithEreignisseRequest(heldid, null, version), cacheHandler);
		File to = getFileFor(new ReturnHeldDatenWithEreignisseRequest(heldid, null, version + 1), cacheHandler);
		try {
			FileUtils.moveFile(from, to);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
