package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.GroupNotFoundException;
import de.failender.dsaonline.exceptions.UserAlreadyExistsException;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private UserHeldenService userHeldenService;

	public UserEntity registerUser(UserRegistration userRegistration) {
		SecurityUtils.checkRight(SecurityUtils.CREATE_USER);


		if(userRegistration.getName() == null || userRegistration.getToken() == null || userRegistration.getGruppe() == null) {
			throw new ValidationException();
		}
		if(this.userRepository.existsByName(userRegistration.getName())) {
			throw new UserAlreadyExistsException();
		}
		GruppeEntity gruppeEntity = gruppeRepository.findByName(userRegistration.getGruppe());
		if(gruppeEntity == null) {
			throw new GroupNotFoundException();
		}
		UserEntity userEntity = new UserEntity();
		userEntity.setGruppe(gruppeEntity);
		userEntity.setName(userRegistration.getName());
		userEntity.setToken(userRegistration.getToken());
		if(userRegistration.getPassword() != null &&!userRegistration.getPassword().isEmpty()) {
			userEntity.setPassword(userRegistration.getPassword());
		}
		userEntity = this.userRepository.save(userEntity);
		userHeldenService.updateHeldenForUser(userEntity);
		return userEntity;

	}
}
