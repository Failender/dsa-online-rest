package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.GroupNotFoundException;
import de.failender.dsaonline.exceptions.UserAlreadyExistsException;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.util.DevInsertTestData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final GruppeRepository gruppeRepository;
	private final UserHeldenService userHeldenService;

	public UserService(UserRepository userRepository, GruppeRepository gruppeRepository, UserHeldenService userHeldenService) {
		this.userRepository = userRepository;
		this.gruppeRepository = gruppeRepository;
		this.userHeldenService = userHeldenService;
	}

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
		userHeldenService.fakeHeldenForUser(userEntity);
		return userEntity;

	}

	public void addUserRole(UserEntity user, String role) {
		Integer roleId = this.userRepository.getRoleId(role);
		this.userRepository.addUserRole(roleId, user.getId());
	}

	public void createUsers(List<DevInsertTestData.UserData> data) {
		data.forEach(
				userData -> {
					if(userRepository.existsByName(userData.getName())) {
						log.info("User with name {} already exists in database", userData.getName());
						return;
					}
					String gruppe = null;
					if(userData.getGruppe() != null) {
						gruppe = userData.getGruppe();
					} else {
						gruppe = this.gruppeRepository.findAll().get(0).getName();
					}
					UserRegistration userRegistration = new UserRegistration(userData.getName(), null, userData.getToken(), gruppe);
					UserEntity userEntity = registerUser(userRegistration);
					userData.roles.forEach(role -> addUserRole(userEntity, role));
				}
		);
	}
}
