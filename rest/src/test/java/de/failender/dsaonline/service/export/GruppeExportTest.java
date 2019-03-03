package de.failender.dsaonline.service.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.restservice.HeldenTest;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class GruppeExportTest extends HeldenTest {


    @Autowired
    private ObjectMapper objectMapper;

    private ExportService exportService;
    @Before
    public void before() {
        exportService = new ExportService(gruppeRepository, objectMapper, Collections.EMPTY_LIST);
    }

    @Test
    @FlywayTest
    public void testExport() throws IOException {
        List<GruppeEntity> gruppen = gruppeRepository.findAll();

        File file = new File("gruppexportadapter.zip");
        FileOutputStream fos = new FileOutputStream(file);
        exportService.doExport(fos);
        fos.close();
        gruppeRepository.deleteAll();

        FileInputStream fis = new FileInputStream(file);
        exportService.provideFullImport(fis);
        fis.close();
        file.delete();
        List<GruppeEntity> imported = gruppeRepository.findAll();

        Assertions.assertThat(imported.size()).isEqualTo(gruppen.size());
        for (GruppeEntity gruppeEntity : gruppen) {
            GruppeEntity imp = imported
                    .stream()
                    .filter(g -> g.getName().equals(gruppeEntity.getName()))
                    .findFirst()
                    .get();

            Assertions.assertThat(ObjectUtils.nullSafeEquals(imp.getDatum(), gruppeEntity.getDatum())).isEqualTo(true);
        }



    }
}
