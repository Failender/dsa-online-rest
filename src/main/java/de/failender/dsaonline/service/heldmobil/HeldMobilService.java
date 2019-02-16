package de.failender.dsaonline.service.heldmobil;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.HeldMobilEntity;
import de.failender.dsaonline.data.repository.HeldMobilRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.rest.dto.HeldMobilInformation;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class HeldMobilService {

    private final HeldRepositoryService heldRepositoryService;
    private final HeldenApi heldenApi;

    private final HeldMobilRepository heldMobilRepository;

    public HeldMobilService(HeldRepositoryService heldRepositoryService, HeldenApi heldenApi, HeldMobilRepository heldMobilRepository) {
        this.heldRepositoryService = heldRepositoryService;
        this.heldenApi = heldenApi;
        this.heldMobilRepository = heldMobilRepository;
    }


    public HeldMobilInformation getMobilInformation(BigInteger heldid) {
        HeldEntity heldEntity = heldRepositoryService.findHeld(heldid);
        Daten daten = heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(heldid, null, heldRepositoryService.findHeldWithLatestVersion(heldEntity).getVersion().getCacheId())).block();

        Optional<HeldMobilEntity> dataOptional = heldMobilRepository.findById(heldid);
        HeldMobilEntity entity = dataOptional.orElseGet(() -> {

            HeldMobilEntity heldMobilEntity = new HeldMobilEntity();
            heldMobilEntity.setHeldid(heldid);
            heldMobilEntity.setAsp(daten.getEigenschaften().getAstralenergie().getAkt().intValue());
            heldMobilEntity.setLep(daten.getEigenschaften().getLebensenergie().getAkt().intValue());
            heldMobilRepository.save(heldMobilEntity);
            return heldMobilEntity;
        });
        HeldMobilInformation heldMobilInformation = new HeldMobilInformation();
        heldMobilInformation.setAsp(entity.getAsp());
        heldMobilInformation.setLep(entity.getLep());
        heldMobilInformation.setMaxAsp(daten.getEigenschaften().getAstralenergie().getAkt().intValue());
        heldMobilInformation.setMaxLep(daten.getEigenschaften().getLebensenergie().getAkt().intValue());
        return heldMobilInformation;
    }
}
