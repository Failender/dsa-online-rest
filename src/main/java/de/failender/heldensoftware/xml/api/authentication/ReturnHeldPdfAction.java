package de.failender.heldensoftware.xml.api.authentication;

import de.failender.heldensoftware.xml.api.requests.ApiRequest;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

public class ReturnHeldPdfAction extends ApiRequest<InputStream> {

	private final BigInteger heldid;

	public ReturnHeldPdfAction(BigInteger heldid) {
		this.heldid = heldid;
	}

	@Override
	public String url() {
		return "https://online.helden-software.de";
	}

	@Override
	public void writeToRequest(Map<String, String> data) {
		data.put("action", "returnheld");
		data.put("format", "pdfintern");
		data.put("heldenid", heldid.toString());
		data.put("opt", "ereignisse");

	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return is;
	}
}
