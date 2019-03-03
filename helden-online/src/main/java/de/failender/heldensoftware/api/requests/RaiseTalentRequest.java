package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class RaiseTalentRequest extends ApiRequest<String> {

	private final TokenAuthentication tokenAuthentication;
	private final BigInteger heldid;
	private final String talent;
	private final int aktwert;

	public RaiseTalentRequest(TokenAuthentication tokenAuthentication, BigInteger heldid, String talent, int aktwert) {
		this.tokenAuthentication = tokenAuthentication;
		this.heldid = heldid;
		this.talent = talent;
		this.aktwert = aktwert;
	}

	@Override
	public Map<String, String> writeRequest() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public String mapResponse(InputStream is) {
		try {
			return IOUtils.toString(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File getCacheFile(File root) {
		return null;
	}

	@Override
	public String requestMethod() {
		return "GET";
	}

	@Override
	public String url() {
		return super.url() + "/steigern/steigeretalent/?token=" + tokenAuthentication.getToken() + "&heldenid=" + heldid + "&talentname=" + talent + "&aktwert=" + aktwert;
	}
}
