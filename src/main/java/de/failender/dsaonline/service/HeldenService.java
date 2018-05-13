package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.rest.helden.HeldenInfo;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HeldenService {

	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserRepository userRepository;

	public List<HeldenInfo> getAllHeldenForCurrentUser() {
		UserEntity user = SecurityUtils.getCurrentUser();
		List<HeldEntity> heldEntities = heldRepository.findByUserIdAndActive(user.getId(), true);
		return heldEntities.stream()
				.map(this::mapToHeldenInfo)
				.collect(Collectors.toList());

	}

	public HeldenInfo mapToHeldenInfo(HeldEntity heldEntity) {
		return new HeldenInfo(heldEntity.getName(), heldEntity.getId().getCreatedDate(), heldEntity.getVersion(), heldEntity.getGruppe().getName(), heldEntity.getId().getId());
	}

	public Daten getHeldenDaten(BigInteger id) {
		Optional<HeldEntity> heldEntityOptional = this.heldRepository.findByIdId(id);
		if(!heldEntityOptional.isPresent()) {
			log.error("Held with id " + id + " could not be found");
			throw new HeldNotFoundException();
		}
		UserEntity user = SecurityUtils.getCurrentUser();
		if(heldEntityOptional.get().getUserId() != user.getId()) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
			UserEntity owningUser = this.userRepository.findById(heldEntityOptional.get().getUserId()).get();
			return apiService.getHeldenDaten(id, heldEntityOptional.get().getVersion(), owningUser.getToken());
		} else {
			return apiService.getHeldenDaten(id, heldEntityOptional.get().getVersion());
		}


	}
}
