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
	private final int version;

	public ReturnHeldPdfRequest(BigInteger heldid, Authentication authentication, int version) {
		this.heldid = heldid;
		this.authentication = authentication;
		this.version = version;
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		authentication.writeToRequest(data);
		data.put("action", "returnheld");
		data.put("format", HeldenApi.Format.pdfintern.toString());
		data.put("heldenid", heldid.toString());
		return data;
	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return is;
	}

	@Override
	public File getCacheFile(File root) {
		File directory = new File(root, "pdf/" + heldid);
		return new File(directory , version + ".pdf");

	}
}
