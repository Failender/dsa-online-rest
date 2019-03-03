package de.failender.dsaonline.service.export;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.data.service.VersionRepositoryService;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.util.VersionService;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class HeldenExportAdapter implements ExportAdapter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExportService.class);
    private final ObjectMapper objectMapper;
    private final HeldRepositoryService heldRepositoryService;
    private final VersionService versionService;
    private final HeldenApi heldenApi;
    private final HeldRepository heldRepository;
    private final VersionRepositoryService versionRepositoryService;

    public HeldenExportAdapter(ObjectMapper objectMapper, HeldRepositoryService heldRepositoryService, VersionService versionService, HeldenApi heldenApi, HeldRepository heldRepository, VersionRepositoryService versionRepositoryService) {
        this.objectMapper = objectMapper;
        this.heldRepositoryService = heldRepositoryService;
        this.versionService = versionService;
        this.heldenApi = heldenApi;
        this.heldRepository = heldRepository;
        this.versionRepositoryService = versionRepositoryService;
    }

    @Override
    public void export(ZipOutputStream zos) throws IOException {
        List<HeldEntity> helden = heldRepository.findByDeletedIsFalse();
        zos.putNextEntry(new ZipEntry("helden.json"));
        zos.write(objectMapper.writeValueAsBytes(helden));
        zos.closeEntry();
        for (HeldEntity heldEntity : helden) {
            for (VersionEntity versionEntity : versionRepositoryService.findVersions(heldEntity)) {
                zos.putNextEntry(new ZipEntry("helden/" + heldEntity.getId() + "." + versionEntity.getVersion() + ".xml"));
                ReturnHeldXmlRequest req = new ReturnHeldXmlRequest(heldEntity.getId(), null, versionEntity.getCacheId());
                heldenApi.provideDownload(req, zos);
                zos.closeEntry();
            }
        }
    }

    @Override
    public void doImport(Map<Integer, Integer> groupMapping, ZipFile file) throws IOException {
        ZipEntry heldenEntry = file.getEntry("helden.json");
        InputStream is = file.getInputStream(heldenEntry);
        List<HeldEntity> helden = objectMapper.readValue(is, new TypeReference<List<HeldEntity>>() {
        });
        helden.forEach(held -> {
            try {
                heldRepositoryService.updateHeldenActive(held.isActive(), held.getId());
                heldRepositoryService.updateHeldenGruppe(groupMapping.get(held.getGruppe().getId()), held.getId());
            } catch (HeldNotFoundException e) {
                log.warn("Skipping import for held with id {} since it does not exist. Did it get deleted?", held.getId());
            }
        });
        Collections.list(file.entries()).stream().filter(entry -> entry.getName().startsWith("helden/")).forEach(entry -> {
            importHeld(file, entry);
        });

    }

    private void importHeld(ZipFile file, ZipEntry entry) {
        try {
            BigInteger id = new BigInteger(entry.getName().split("/")[1].split("\\.")[0]);

            String xml = IOUtils.toString(file.getInputStream(entry), "UTF-8");
            versionService.saveHeld(id, xml);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HeldNotFoundException e) {
        }
    }
}
