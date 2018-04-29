package de.failender.dsaonline.rest.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.repository.HeldRepository;
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
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private ApiService apiService;


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private UserService userService;

	@GetMapping("helden")
	public List<Held> getHelden() {
		return apiService.getAllHelden();
	}

	@GetMapping("held/{id}")
	public String getHeld(@PathVariable("id")BigInteger id) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();

		return om.writeValueAsString(apiService.getHeldenDaten(id));
	}

	@GetMapping("helden/all")
	public List<Held> getAllHelden() {

		String xml = "<helden>" + heldRepository.findAll()
				.stream()
				.map(held -> held.getXml())
				.reduce((a,b) -> a+b)
				+ "</helden>";

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Helden.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Helden daten = (Helden) jaxbUnmarshaller.unmarshal(new StringReader(xml));
			return daten.getHeld();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}


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
