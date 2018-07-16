package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ReturnHeldXmlRequest extends ApiRequest<String> {

	private final BigInteger heldid;
	private final Authentication authentication;
	private final int version;

	public ReturnHeldXmlRequest(BigInteger heldid, Authentication authentication, int version) {
		this.heldid = heldid;
		this.authentication = authentication;
		this.version = version;
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

	@Override
	public File getCacheFile(File root) {
		File directory = new File(root, "xml/" + heldid);
		if(!directory.exists()) {
			directory.mkdirs();
		}
 		return  new File(directory, version + ".xml");
	}
}
