package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.JaxbUtil;
import de.failender.heldensoftware.xml.currentrights.Rechte;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class PermissionRequest extends ApiRequest<Rechte> {

	private final String token;

	public PermissionRequest(String token) {
		this.token = token;
	}

	@Override
	public Map<String, String> writeRequest() {
		return Collections.emptyMap();
	}

	@Override
	public Rechte mapResponse(InputStream is) {
		try {
			return (Rechte) JaxbUtil.getUnmarshaller(Rechte.class).unmarshal(is);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File getCacheFile(File root) {
		return null;
	}

	@Override
	public String url() {
		return super.url() + "/tokens/getcurrentrights/?token=" + token;
	}
}
