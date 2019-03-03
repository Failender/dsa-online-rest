package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReturnHeldXmlRequest extends IdCachedRequest<String> {

	private final Authentication authentication;


	public ReturnHeldXmlRequest(BigInteger heldid, Authentication authentication, UUID cacheId) {
		super(cacheId, heldid, false);
		this.authentication = authentication;
	}

	public ReturnHeldXmlRequest(BigInteger heldid, Authentication authentication, UUID cacheId, boolean ignoreCache) {
		super(cacheId, heldid, ignoreCache);
		this.authentication = authentication;
	}



	@Override
	public String fileExtension() {
		return "xml";
	}

	@Override
	protected String cacheFolder() {
		return "xml";
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		data.put("action", "returnheld");
		data.put("format", HeldenApi.Format.heldenxml.toString());
		data.put("heldenid", heldid.toString());
		if(authentication != null) {
			authentication.writeToRequest(data);
		}
		return data;
	}

	@Override
	public String mapResponse(InputStream is) {
		try {
			return org.apache.commons.io.IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(is);
		}

	}


}
