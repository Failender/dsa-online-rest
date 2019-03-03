package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConvertingRequest extends ApiRequest<InputStream> {

	private final HeldenApi.Format format;
	private final String xml;
	private final long stand;
	private final long key;

	public ConvertingRequest(HeldenApi.Format format, String xml) {
		this.format = format;
		this.xml = xml;
		try {
			Document document = XmlUtil.documentFromString(xml);
			Node node = document.getDocumentElement().getFirstChild();
			while(!(node instanceof Element)) {
				node = node.getNextSibling();
			}
			Element element = (Element) node;
			String stand =element.getAttribute("stand");
			this.stand = Long.valueOf(stand);
			this.key = Long.valueOf(element.getAttribute("key"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String url() {
		return super.url() + "/converter/?format=" + format;
	}

	@Override
	public Map<String, String> writeRequest() {
		Map<String, String> data = new HashMap<>();
		data.put("held", xml);

		return data;
	}

	@Override
	public InputStream mapResponse(InputStream is) {
		return is;
	}

	@Override
	public File getCacheFile(File root) {
		return new File(root, "converting/" + key + "/" + stand + "." + format);
	}

	public HeldenApi.Format getFormat() {
		return format;
	}
}