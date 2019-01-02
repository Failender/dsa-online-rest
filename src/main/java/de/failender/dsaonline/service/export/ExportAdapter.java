package de.failender.dsaonline.service.export;

import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public interface ExportAdapter {

    void export(ZipOutputStream zos) throws IOException;
    void doImport(Map<Integer, Integer> groupMapping, ZipFile zipFile) throws IOException;
}
