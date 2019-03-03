package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReturnHeldPdfRequest extends IdCachedRequest<InputStream> {

	private final Authentication authentication;

	public ReturnHeldPdfRequest(BigInteger heldid, Authentication authentication, UUID cacheId) {
		super(cacheId, heldid, false);

		this.authentication = authentication;
	}

	@Override
	public String fileExtension() {
		return "pdf";
	}

	@Override
	protected String cacheFolder() {
		return "pdf";
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		if(authentication != null) {
			authentication.writeToRequest(data);
		}
		data.put("action", "returnheld");
		data.put("format", HeldenApi.Format.pdfintern.toString());
		data.put("heldenid", heldid.toString());
		return data;
	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return is;
	}

}
