package de.failender.dsaonline.rest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.service.MeisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/meister")
public class MeisterController {

	@Autowired
	private MeisterService meisterService;


	@GetMapping("gruppe/{gruppeId}")
	public List<UserEntity> getMeisterForGruppe(@PathVariable int gruppeId) {
		return meisterService.getMeisterForGruppe(gruppeId);
	}

	@PostMapping("gruppe/{gruppeId}/{userId}")
	public void addMeisterForGruppe(@PathVariable int gruppeId, @PathVariable int userId) {
		meisterService.addMeisterForGruppe(gruppeId, userId);
	}

	@DeleteMapping("gruppe/{gruppeId}/{userId}")
	public void removeMeisterForGruppe(@PathVariable int gruppeId, @PathVariable int userId) {
		meisterService.removeMeisterForGruppe(gruppeId, userId);
	}
}
