package de.failender.heldensoftware.api.requests;

import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;
import de.failender.heldensoftware.xml.datenxml.Daten;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ReturnHeldDatenRequest extends ApiRequest<Daten> {

	private final BigInteger heldid;
	private final Authentication authentication;
	private final int version;

	public ReturnHeldDatenRequest(BigInteger heldid, Authentication authentication, int version) {
		this.heldid = heldid;
		this.authentication = authentication;
		this.version = version;
	}


	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		if(authentication != null) {
			authentication.writeToRequest(data);
		}
		data.put("action", "returnheld");
		data.put("format", HeldenApi.Format.datenxml.toString());
		data.put("heldenid", heldid.toString());
		return data;
	}

	@Override
	public Daten mapResponse(InputStream is) {
		Unmarshaller unmarshaller = JaxbUtil.getUnmarshaller(Daten.class);
		try {
			return (Daten) unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}

	}

	@Override
	public File getCacheFile(File root) {
		File directory = new File(root, "daten/" + heldid);
		if(directory.exists()){
			directory.mkdir();
		}

		return new File(directory, version + ".xml");
	}
}
