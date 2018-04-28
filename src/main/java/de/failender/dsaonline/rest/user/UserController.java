package de.failender.dsaonline.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserHeldenService userHeldenService;

	@Autowired
	private GruppeRepository gruppeRepository;


	@GetMapping("helden")
	public List<Held> getAllHelden() {
		return apiService.getAllHelden();
	}

	@GetMapping("held/{id}")
	public String getHeld(@PathVariable("id")BigInteger id) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();

		return om.writeValueAsString(apiService.getHeldenDaten(id));
	}

	@PostMapping("register")
	public ResponseEntity<?> registerUser(@RequestBody UserRegistration userRegistration) {
		if(userRegistration.getName() == null || userRegistration.getToken() == null || userRegistration.getGruppe() == null) {
			return ResponseEntity.badRequest().build();
		}
		if(this.userRepository.existsByName(userRegistration.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		GruppeEntity gruppeEntity = gruppeRepository.findByName(userRegistration.getGruppe());
		if(gruppeEntity == null) {
			return ResponseEntity.notFound().build();
		}
		UserEntity userEntity = new UserEntity();
		userEntity.setGruppe(gruppeEntity);
		userEntity.setName(userRegistration.getName());
		userEntity.setToken(userRegistration.getToken());
		if(userRegistration.getPassword() != null &&!userRegistration.getPassword().isEmpty()) {
			userEntity.setPassword(userRegistration.getPassword());
		}
		this.userRepository.save(userEntity);
		userHeldenService.updateHeldenForUser(userEntity);
		return ResponseEntity.ok().build();


	}





}
