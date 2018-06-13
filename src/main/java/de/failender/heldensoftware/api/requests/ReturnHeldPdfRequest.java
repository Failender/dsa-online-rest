package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ReturnHeldPdfRequest extends ApiRequest<InputStream> {

	private final BigInteger heldid;
	private final Authentication authentication;

	public ReturnHeldPdfRequest(BigInteger heldid, Authentication authentication) {
		this.heldid = heldid;
		this.authentication = authentication;
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		authentication.writeToRequest(data);
		data.put("action", "returnheld");
		data.put("format", HeldenApi.Format.pdfinternal.toString());
		data.put("heldenid", heldid.toString());
		return data;
	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return is;
	}

	@Override
	public File getCacheFile(File root) {
		return new File(root, "pdf/" + heldid + ".pdf");
	}
}
