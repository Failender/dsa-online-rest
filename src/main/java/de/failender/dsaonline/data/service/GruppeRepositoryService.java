package de.failender.dsaonline.data.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import org.springframework.stereotype.Service;

@Service
public class GruppeRepositoryService {

	private final GruppeRepository gruppeRepository;

	public GruppeRepositoryService(GruppeRepository gruppeRepository) {
		this.gruppeRepository = gruppeRepository;
	}

	public GruppeEntity findById(int id) {
		return gruppeRepository.findById(id).get();
	}
}
