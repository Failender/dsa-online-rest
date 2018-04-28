package de.failender.dsaonline.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.api.HeldenSoftwareAPI;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/helden")
public class HeldenController {

	@Autowired
	private HeldenSoftwareAPI heldenSoftwareAPI;


	@GetMapping
	public List<Held> getAllHelden() {
		return heldenSoftwareAPI.getAllHelden();
	}

	@GetMapping("{id}")
	public String getHeld(@PathVariable("id")BigInteger id) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(heldenSoftwareAPI.getHeldenDaten(id));
	}
}
