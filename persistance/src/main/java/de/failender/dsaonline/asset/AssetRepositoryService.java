package de.failender.dsaonline.asset;

import de.failender.dsaonline.data.service.KampagneRepositoryService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetRepositoryService {

	private final AssetRepository assetRepository;
	private final KampagneRepositoryService kampagneRepositoryService;
	public AssetRepositoryService(AssetRepository assetRepository, KampagneRepositoryService kampagneRepositoryService) {
		this.assetRepository = assetRepository;
		this.kampagneRepositoryService = kampagneRepositoryService;
	}

	public List<AssetEntity> getAssetsForKampagne(int kampagne) {
		if(kampagneRepositoryService.isMeisterForKampagne(kampagne)) {
			return assetRepository.findByKampagne(kampagne);
		} else {
			return assetRepository.findByKampagneAndHiddenFalse(kampagne);
		}
	}

	public AssetEntity save(AssetEntity assetEntity) {
		if(!kampagneRepositoryService.isMeisterForKampagne(assetEntity.getKampagne())) {
			throw new AccessDeniedException("Assets können nur für Kampagnen angelegt werden für die der Nutzer Meister ist");
		}
		return assetRepository.save(assetEntity);
	}

	public void delete(AssetEntity assetEntity) {
		if(!kampagneRepositoryService.isMeisterForKampagne(assetEntity.getKampagne())) {
			throw new AccessDeniedException("Assets können nur für Kampagnen gelöscht werden für die der Nutzer Meister ist");
		}
		assetRepository.delete(assetEntity);
	}


	public AssetEntity getImage(Integer id) {
		return assetRepository.findById(id).get();
	}
}
