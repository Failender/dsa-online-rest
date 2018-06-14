package de.failender.heldensoftware.api.requests;

import de.failender.dsaonline.exceptions.ExchangeException;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;

import java.io.*;
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
		data.put("format", HeldenApi.Format.pdfintern.toString());
		data.put("heldenid", heldid.toString());
		authentication.writeToRequest(data);
		return data;
	}

	@Override
	public String mapResponse(InputStream is) {
		Writer swriter = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader =
				new BufferedReader(new InputStreamReader(is));
		int count;
		try {
			while ((count = reader.read(buffer)) != -1) {

				swriter.write(buffer, 0, count);

			}
			reader.close();
		} catch (IOException e) {
			throw new ExchangeException(e);
		}
		return swriter.toString();

	}

	@Override
	public File getCacheFile(File root) {
		return new File(root, "xml/" + heldid + "/" + version + ".xml");
	}
}
