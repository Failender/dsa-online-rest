package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.JaxbUtil;
import de.failender.heldensoftware.api.CorruptXmlException;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.Authentication;
import de.failender.heldensoftware.xml.datenxml.Daten;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReturnHeldDatenRequest extends IdCachedRequest<Daten> {


	private final Authentication authentication;

	public ReturnHeldDatenRequest(BigInteger heldid, Authentication authentication, UUID cacheId) {
		this(heldid, authentication,cacheId, false);
	}

	public ReturnHeldDatenRequest(BigInteger heldid, Authentication authentication, UUID cacheId, boolean ignoreCache) {
		super(cacheId, heldid, ignoreCache);
		this.authentication = authentication;
	}


	@Override
	public String fileExtension() {
		return "xml";
	}

	@Override
	protected String cacheFolder() {
		return "daten";
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
}
