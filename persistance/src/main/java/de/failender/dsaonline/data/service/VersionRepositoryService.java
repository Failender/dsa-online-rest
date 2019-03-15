// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.data.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.ApiRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VersionRepositoryService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VersionRepositoryService.class);
	private final VersionRepository versionRepository;
	private final HeldenApi heldenApi;
	private final VersionFixService versionFixService;
	private final SecurityUtils securityUtils;

	public VersionRepositoryService(VersionRepository versionRepository, HeldenApi heldenApi, VersionFixService versionFixService, SecurityUtils securityUtils) {
		this.versionRepository = versionRepository;
		this.heldenApi = heldenApi;
		this.versionFixService = versionFixService;
		this.securityUtils = securityUtils;
	}

	public VersionEntity findVersion(HeldEntity held, int version) {
		try {
			Optional<VersionEntity> versionEntityOptional = versionRepository.findByVersionAndHeldid(version, held.getId());
			if (!versionEntityOptional.isPresent()) {
				log.error("Held with id {} and version {} could not be found", held.getId(), version);
				throw new HeldNotFoundException(held.getId(), version);
			}
			return versionEntityOptional.get();
		} catch (Exception e) {
			versionFixService.fixVersions();
			Optional<VersionEntity> versionEntityOptional = versionRepository.findByVersionAndHeldid(version, held.getId());
			if (!versionEntityOptional.isPresent()) {
				log.error("Held with id {} and version {} could not be found", held.getId(), version);
				throw new HeldNotFoundException(held.getId(), version);
			}
			return versionEntityOptional.get();
		}
	}

	public VersionEntity findLatestVersion(HeldEntity held) {
		return versionRepository.findFirstByHeldidOrderByVersionDesc(held.getId());
	}

	public List<VersionEntity> findVersionsForHeldDescending(HeldEntity held) {
		return versionRepository.findByHeldidOrderByVersionDesc(held.getId());
	}

	public List<VersionEntity> findVersions(HeldEntity held) {
		return versionRepository.findByHeldid(held.getId());
	}

	public Optional<VersionEntity> findVersionByHeldidAndCreated(HeldEntity heldEntity, Date created) {
		return versionRepository.findByHeldidAndCreatedDate(heldEntity.getId(), created);
	}

	public VersionEntity saveVersion(HeldEntity heldEntity, VersionEntity version) {
		performEditCheck(heldEntity, version);
		return versionRepository.save(version);
	}

	public VersionEntity saveVersionAndFlush(HeldEntity heldEntity, VersionEntity version) {
		performEditCheck(heldEntity, version);
		return versionRepository.saveAndFlush(version);
	}

	public long count() {
		return versionRepository.count();
	}

	public Iterable<VersionEntity> findAll() {
		return versionRepository.findAll();
	}

	public void deleteVersion(HeldEntity held, int version) {
		if (version == 1) {
			//Dont delete the last existing version
			if (versionRepository.countByHeldid(held.getId()) == 1) {
				return;
			}
		}
		VersionEntity versionEntity = findVersion(held, version);
		deleteVersion(held, versionEntity);
	}

	public void deleteVersion(HeldEntity held, VersionEntity versionEntity) {
		performEditCheck(held, versionEntity);
		for (ApiRequest apiRequest : HeldenApi.getDataApiRequests(versionEntity.getCacheId())) {
			heldenApi.getCacheHandler().removeCache(apiRequest);
		}
		versionRepository.delete(versionEntity);
		List<VersionEntity> newerVersions = versionRepository.findByVersionGreaterThanOrderByVersionAsc(versionEntity.getVersion());
		for (VersionEntity entity : newerVersions) {
			entity.setVersion(entity.getVersion() - 1);
		}
		versionRepository.saveAll(newerVersions);
	}

	private void performEditCheck(HeldEntity held, VersionEntity versionEntity) {
		if (!versionEntity.getHeldid().equals(held.getId())) {
			throw new IllegalArgumentException("Wrong held expected " + versionEntity.getHeldid() + " got " + versionEntity.getHeldid());
		}
		securityUtils.canCurrentUserEditHeld(held);
	}
}