package de.failender.dsaonline.util;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.IdCachedRequest;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeldenSoftwareIntegration {

    public static void provideDownload(HeldenApi heldenApi, IdCachedRequest<?> request, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String fileName = "download." + request.fileExtension();
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        try {
            heldenApi.provideDownload(request, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
