package de.failender.dsaonline.rest.gruppen;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/gruppen")
public class GruppenController {


	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private HeldRepository heldRepository;

	@GetMapping
	public List<DropdownData> getAllGruppen() {
		return this.gruppeRepository.findAll()
				.stream()
				.map(gruppe -> new DropdownData(gruppe.getName(), gruppe.getId()))
				.collect(Collectors.toList());
	}

	@PostMapping("{heldid}/{gruppeid}")
	public void editHeldenGruppe(@PathVariable BigInteger heldid, @PathVariable Integer gruppeid) {
		SecurityUtils.checkLogin();
		Optional<HeldEntity> heldEntityOptional = this.heldRepository.findById(heldid);
		if(!heldEntityOptional.isPresent()) {
			throw new EntityNotFoundException();
		}
		UserEntity userEntity = SecurityUtils.getCurrentUser();
		if(heldEntityOptional.get().getUserId() != userEntity.getId()) {
			SecurityUtils.checkRight(SecurityUtils.EDIT_ALL);
		}
		this.heldRepository.updateHeldenGruppe(gruppeid,heldid);
	}
}
