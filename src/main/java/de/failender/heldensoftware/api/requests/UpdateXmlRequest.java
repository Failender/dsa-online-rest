package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.api.authentication.TokenAuthentication;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateXmlRequest extends ApiRequest<String> {

    private final TokenAuthentication authentication;
    private final String xml;

    public UpdateXmlRequest(TokenAuthentication authentication, String xml) {
        this.authentication = authentication;
        this.xml = xml;


    }

    @Override
    public Map<String, String> writeRequest() {
        Map<String, String> map = new HashMap<>();
        map.put("held", xml);
        return map;
    }

    @Override
    public String mapResponse(InputStream is) {
        return "" ;
    }

    @Override
    public File getCacheFile(File root) {
        return null;
    }

    @Override
    public String url() {
        return super.url() + "/held/upload?token=" + authentication.getToken();

    }
}
