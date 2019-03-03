package de.failender.dsaonline.service.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.restservice.HeldenTest;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class UserExportAdapterTest extends HeldenTest {

    @Before
    public void prepare() {
        userExportAdapter = new UserExportAdapter(new ObjectMapper(), userService, userRepository);
    }

    private UserExportAdapter userExportAdapter;

    @Test
    @FlywayTest
    public void testExport() throws IOException {
        String grpName = "GRP";
        GruppeEntity gruppeEntity = new GruppeEntity();
        gruppeEntity.setName(grpName);
        gruppeEntity.setId(1);
        gruppeEntity = gruppeRepository.save(gruppeEntity);

        String grpNameTwo = "GRP2";
        GruppeEntity gruppeEntity1 = new GruppeEntity();
        gruppeEntity1.setName(grpNameTwo);
        gruppeEntity1.setId(2);
        gruppeEntity1 = gruppeRepository.save(gruppeEntity1);

        UserEntity userEntity = new UserEntity();
        userEntity.setToken("DEMO");
        userEntity.setName("DEMO");
        userEntity.setGruppe(gruppeEntity);
        userRepository.save(userEntity);

        UserEntity userEntity1 = new UserEntity();
        userEntity1.setGruppe(gruppeEntity1);
        userEntity1.setName("DEMO2");
        userRepository.save(userEntity1);

        File file = new File("userexportadater.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
        userExportAdapter.export(zipOutputStream);
        zipOutputStream.close();
        userRepository.deleteAll();
        Assertions.assertThat(userRepository.count()).isEqualTo(0);

        ZipFile zf = new ZipFile(file);
        userExportAdapter.doImport(null, zf);
        zf.close();
        file.delete();
        userEntity = userRepository.findByName("DEMO");


        Assertions.assertThat(userEntity).isNotNull();
        Assertions.assertThat(userEntity.getName()).isEqualTo("DEMO");
        Assertions.assertThat(userEntity.getToken()).isEqualTo("DEMO");

        userEntity1 = userRepository.findByName("DEMO2");
        Assertions.assertThat(userEntity1).isNotNull();
        Assertions.assertThat(userEntity1.getName()).isEqualTo("DEMO2");
        Assertions.assertThat(userEntity1.getToken()).isNull();


    }
}
