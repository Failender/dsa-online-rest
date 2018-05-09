package de.failender.dsaonline.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.UserService;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private ApiService apiService;


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@GetMapping("helden")
	public List<Held> getHelden() {
		return apiService.getAllHelden();
	}

	@GetMapping("held/{id}")
	public String getHeld(@PathVariable("id")BigInteger id) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		SecurityUtils.checkLogin();
		UserEntity user = SecurityUtils.getCurrentUser();
		Optional<HeldEntity> heldEntityOptional = this.heldRepository.findFirstByIdOrderByVersion(id);
		if(!heldEntityOptional.isPresent()) {
			throw new HeldNotFoundException();
		}
		HeldEntity held = heldEntityOptional.get();
		if(held.getUserId() != user.getId()) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		}
		return om.writeValueAsString(apiService.getHeldenDaten(id, held.getVersion()));
	}

	@GetMapping("helden/all")
	public List<Held> getAllHelden() {
		SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		return this.userRepository.findAll()
				.stream()
				.map(user -> this.apiService.getAllHelden(user.getToken()))
				.flatMap(e-> e.stream())
				.collect(Collectors.toList());


	}

	@PostMapping("register")
	public ResponseEntity<?> registerUser(@RequestBody UserRegistration userRegistration) {
		userService.registerUser(userRegistration);
		return ResponseEntity.ok().build();
	}

	@GetMapping("login")
	public List<String> login() {
		return SecurityUtils.getAuthorities();
	}





}
