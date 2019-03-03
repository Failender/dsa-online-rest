package de.failender.heldensoftware.api.requests;

import de.failender.heldensoftware.JaxbUtil;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.xml.listtalente.ListTalente;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class ListTalenteRequest extends ApiRequest<ListTalente> {

    private final TokenAuthentication tokenAuthentication;
    private final BigInteger heldid;
    public ListTalenteRequest(TokenAuthentication tokenAuthentication, BigInteger heldid) {
        this.tokenAuthentication = tokenAuthentication;
        this.heldid = heldid;
    }

    @Override
    public Map<String, String> writeRequest() {

        return Collections.EMPTY_MAP;

    }

    @Override
    public ListTalente mapResponse(InputStream is) {
        try {
            return (ListTalente) JaxbUtil.getUnmarshaller(ListTalente.class).unmarshal(is);
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
        return super.url() + "/steigern/listtalente/?token=" + tokenAuthentication.getToken() + "&heldenid=" + heldid;
    }

    @Override
    public String requestMethod() {
        return "GET";
    }
}
