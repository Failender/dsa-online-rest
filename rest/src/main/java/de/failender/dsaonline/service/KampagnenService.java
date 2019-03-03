package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.KampagneEntity;
import de.failender.dsaonline.data.service.GruppeRepositoryService;
import de.failender.dsaonline.data.service.KampagneRepositoryService;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class KampagnenService {

	private final KampagneRepositoryService kampagneRepositoryService;
	private final GruppeRepositoryService gruppeRepositoryService;
	private final SecurityUtils securityUtils;

	public KampagnenService(KampagneRepositoryService kampagneRepositoryService, GruppeRepositoryService gruppeRepositoryService, SecurityUtils securityUtils) {
		this.kampagneRepositoryService = kampagneRepositoryService;
		this.gruppeRepositoryService = gruppeRepositoryService;
		this.securityUtils = securityUtils;
	}

	public List<KampagneEntity> findKampagneByGruppe(int gruppe) {
		return kampagneRepositoryService.findByGruppe(gruppeRepositoryService.findById(gruppe));
	}

	public void createKampagne(String name, int gruppeId) {
		securityUtils.checkRight(SecurityUtils.EDIT_KAMPAGNE, gruppeId);
		KampagneEntity entity = new KampagneEntity();
		entity.setGruppeId(gruppeId);
		entity.setName(name);
		entity.setCreatedDate(new Date());
		kampagneRepositoryService.saveKampagne(entity);
	}

	public void deleteKampagne(int kampagneid) {
		if(!SecurityUtils.hasRight(SecurityUtils.EDIT_KAMPAGNE)) {
			KampagneEntity kampagneEntity = kampagneRepositoryService.findKampagneById(kampagneid);
			securityUtils.checkIsUserMeisterForGruppe(kampagneEntity.getGruppeId());
		}
		kampagneRepositoryService.deleteKampagne(kampagneid);
	}

	public KampagneEntity getKampagneById(int kampagneid) {
		return kampagneRepositoryService.findKampagneById(kampagneid);
	}
}
