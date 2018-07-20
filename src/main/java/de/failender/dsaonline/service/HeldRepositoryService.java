package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.rest.helden.HeldWithVersion;
import de.failender.dsaonline.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HeldRepositoryService {

	@Autowired
	private VersionRepository versionRepository;

	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private SecurityUtils securityUtils;


	public void updateHeldenGruppe(Integer gruppeid, BigInteger heldid) {
		heldRepository.updateHeldenGruppe(gruppeid, heldid);
	}

	public HeldWithVersion findHeldWithLatestVersion(BigInteger heldid) {
		HeldEntity held = findHeld(heldid);
		return findHeldWithLatestVersion(held);
	}

	public HeldWithVersion findHeldWithLatestVersion(HeldEntity held) {
		VersionEntity versionEntity = findLatestVersion(held);
		return new HeldWithVersion(held, versionEntity);
	}

	public VersionEntity findLatestVersion(BigInteger heldid) {
		return versionRepository.findFirstByHeldidOrderByVersionDesc(heldid);
	}

	public VersionEntity findLatestVersion(HeldEntity held) {
		return findLatestVersion(held.getId());
	}

	public HeldWithVersion findHeldWithVersion(BigInteger heldid, int version) {
		HeldEntity held = findHeld(heldid);
		VersionEntity versionEntity = findVersion(heldid, version);
		return new HeldWithVersion(held, versionEntity);
	}

	public VersionEntity findVersion(BigInteger heldid, int version) {
		Optional<VersionEntity> versionEntityOptional = versionRepository.findByVersionAndHeldid(version, heldid);
		if (!versionEntityOptional.isPresent()) {
			log.error("Held with id {} and version {}could not be found", heldid, version);
			throw new HeldNotFoundException(heldid, version);
		}
		return versionEntityOptional.get();

	}

	public List<HeldEntity> findByUserId(int userid) {
		return heldRepository.findByUserIdAndDeleted(userid, false);
	}

	public HeldEntity findHeld(BigInteger heldid) {
		Optional<HeldEntity> heldEntityOptional = heldRepository.findById(heldid);
		if (!heldEntityOptional.isPresent()) {
			log.error("Held with id {} could not be found", heldid);
			throw new HeldNotFoundException(heldid);
		}
		securityUtils.canCurrentUserViewHeld(heldEntityOptional.get());
		return heldEntityOptional.get();
	}

	public List<HeldEntity> findByGruppeId(Integer gruppeId) {
		return heldRepository.findByGruppeId(gruppeId);
	}

	public void saveVersion(VersionEntity versionEntity) {
		versionRepository.save(versionEntity);
	}

	public void saveHeld(HeldEntity heldEntity) {
		heldRepository.save(heldEntity);
	}

	public void updateHeldenPublic(boolean isPublic, BigInteger heldid) {

		heldRepository.updateHeldenPublic(isPublic, heldid);
	}

	public List<HeldEntity> getHeldenForGruppe(int gruppeid) {
		return heldRepository.findByGruppeId(gruppeid);
	}

	public List<BigInteger> getAllHeldenIds() {
		return heldRepository.getAllHeldenIds();
	}
}
