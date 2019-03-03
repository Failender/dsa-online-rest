package de.failender.heldensoftware.api.requests;
import de.failender.heldensoftware.JaxbUtil;
import de.failender.heldensoftware.api.CorruptXmlException;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.xml.heldenliste.Helden;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class GetAllHeldenRequest extends ApiRequest<Helden> {

	private final TokenAuthentication authentication;

	public GetAllHeldenRequest(TokenAuthentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		if(authentication != null) {
			authentication.writeToRequest(data);
		}
		data.put("action", "listhelden");
		return data;
	}


	@Override
	public Helden mapResponse(InputStream is) {
		Unmarshaller unmarshaller = JaxbUtil.getUnmarshaller(Helden.class);
		try {
			return (Helden) unmarshaller.unmarshal(new InputStreamReader(is));
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	@Override
	public File getCacheFile(File root) {
		File directory = new File(root, "helden");
		if(!directory.exists()) {
			directory.mkdirs();
		}
		return new File(directory, authentication.getToken() + ".xml");
	}
}
