package de.failender.dsaonline.data.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.KampagneEntity;
import de.failender.dsaonline.data.repository.KampagneRepository;
import de.failender.dsaonline.exceptions.KampagneNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KampagneRepositoryService {

	private final KampagneRepository kampagneRepository;

	public KampagneRepositoryService(KampagneRepository kampagneRepository) {
		this.kampagneRepository = kampagneRepository;
	}

	public List<KampagneEntity> findByGruppe(GruppeEntity gruppeEntity) {
		return kampagneRepository.findByGruppeId(gruppeEntity.getId());
	}

	public void saveKampagne(KampagneEntity kampagneEntity) {
		kampagneRepository.save(kampagneEntity);
	}

	public void deleteKampagne(int kampagneid) {
		kampagneRepository.deleteById(kampagneid);
	}

	public KampagneEntity findKampagneById(int kampagneid) {
		return kampagneRepository.findById(kampagneid).orElseThrow(() -> new KampagneNotFoundException());
	}
}
