package de.failender.dsaonline.rest.user;

import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
	private HeldenService heldenService;

	@Autowired
	private UserService userService;

	@GetMapping("helden")
	public List<Held> getHelden() {
		return apiService.getAllHelden();
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
