package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.dto.UserData;
import de.failender.dsaonline.rest.dto.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.UserService;
import de.failender.dsaonline.util.SelectData;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.GetAllHeldenRequest;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.failender.dsaonline.security.SecurityUtils.getAuthentication;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private HeldenApi heldenApi;

	@Autowired
	private UserRepository userRepository;


	@Autowired
	private UserService userService;

	@GetMapping("helden")
	public List<Held> getHelden() {
		return heldenApi.request(new GetAllHeldenRequest(getAuthentication())).block().getHeld();
	}

	@GetMapping("helden/all")
	public List<Held> getAllHelden() {
		SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		return this.userRepository.findAll()
				.stream()
				.map(user -> this.heldenApi.request(new GetAllHeldenRequest(getAuthentication())).block().getHeld())
				.flatMap(e -> e.stream())
				.collect(Collectors.toList());


	}

	@PostMapping("register")
	public ResponseEntity<?> registerUser(@RequestBody UserRegistration userRegistration) {
		userService.registerUser(userRegistration);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "create", consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public void registerUsers(@RequestBody List<UserData> data) {
		userService.createUsers(data);
	}

	@GetMapping("login")
	public List<String> login() {
		return SecurityUtils.getAuthorities();
	}

	@GetMapping()
	public List<SelectData> getAllUser() {
		SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		return userRepository.findAll()
				.stream()
				.map(user -> new SelectData(user.getName(), user.getId()))
				.collect(Collectors.toList());
	}

}
