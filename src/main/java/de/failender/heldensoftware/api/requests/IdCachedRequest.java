package de.failender.heldensoftware.api.requests;

import java.io.File;
import java.math.BigInteger;
import java.util.UUID;

public abstract class IdCachedRequest<T>  extends ApiRequest<T>{
	private final UUID cacheId;
	protected final BigInteger heldid;
	private final boolean ignoreCache;

	public IdCachedRequest(UUID cacheId, BigInteger heldid, boolean ignoreCache) {
		if(!ignoreCache && cacheId == null) {
			throw new IllegalArgumentException("CacheId can not be null");
		}
		this.cacheId = cacheId;
		this.heldid = heldid;
		this.ignoreCache = ignoreCache;
	}

	protected abstract String cacheFolder();
	public abstract String fileExtension();


	@Override
	public File getCacheFile(File root) {
		if(ignoreCache) {
			return null;
		}
		File directory = new File(root, cacheFolder() + "/" + heldid);
		if(directory.exists()){
			directory.mkdir();
		}

		return new File(directory, cacheId + "." + fileExtension());
	}
}
