package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeisterService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private GruppeRepository gruppeRepository;

	public List<UserEntity> getMeisterForGruppe( int gruppeId) {
		return gruppeRepository.findMeisterForGruppe(gruppeId);
	}


	public void addMeisterForGruppe( int gruppeId,  int userId) {
		SecurityUtils.checkRight(SecurityUtils.CREATE_USER);
		if(!userRepository.getUserRights(userId).contains(SecurityUtils.MEISTER)) {
			userService.addUserRole(userRepository.findById(userId).get(), SecurityUtils.ROLE_MEISTER);
		}
		userRepository.addMeisterForGruppe(userId, gruppeId);
	}


	public void removeMeisterForGruppe(int gruppeId,  int userId) {
		SecurityUtils.checkRight(SecurityUtils.CREATE_USER);
		userRepository.removeMeisterForGruppe(userId, gruppeId);
	}
}
