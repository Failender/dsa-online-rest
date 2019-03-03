package de.failender.dsaonline.service.export;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class AbenteuerExportAdapter implements ExportAdapter {
    @Override
    public void export(ZipOutputStream zos) throws IOException {

    }

    @Override
    public void doImport(Map<Integer, Integer> groupMapping, ZipFile zipFile) throws IOException {

    }
}
