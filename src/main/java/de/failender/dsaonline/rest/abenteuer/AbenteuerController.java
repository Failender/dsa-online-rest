package de.failender.dsaonline.rest.abenteuer;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.abenteuer.AbenteuerEntity;
import de.failender.dsaonline.data.entity.abenteuer.BonusApEntity;
import de.failender.dsaonline.data.entity.abenteuer.SeEntity;
import de.failender.dsaonline.data.repository.AbenteuerRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/abenteuer")
public class AbenteuerController {

	@Autowired
	private AbenteuerRepository abenteuerRepository;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private HeldRepository heldRepository;


	private static final BigInteger NEGATIVE = BigInteger.valueOf(-1);
	@PostMapping
	public void createAbenteuer(@RequestBody AbenteuerDto abenteuerDto) {
		securityUtils.checkIsUserMeisterForGruppe(abenteuerDto.getGruppe());
		AbenteuerEntity abenteuerEntity = new AbenteuerEntity();
		abenteuerEntity.setName(abenteuerDto.getName());
		abenteuerEntity.setAp(abenteuerDto.getBonusAll().getAp());
		abenteuerEntity.setGruppeId(abenteuerDto.getGruppe());
		abenteuerEntity = abenteuerRepository.save(abenteuerEntity);
		for (String se : abenteuerDto.getBonusAll().getSes()) {
			SeEntity seEntity = new SeEntity();
			seEntity.setHeld(NEGATIVE);
			seEntity.setSe(se);
			abenteuerEntity.getSes().add(seEntity);
			seEntity.setAbenteuerId(abenteuerEntity.getId());
		}
		for (Map.Entry<String, AbenteuerDto.Bonus> entry : abenteuerDto.getBonus().entrySet()) {
			HeldEntity heldEntity = heldRepository.findByName(entry.getKey());
			BigInteger id = heldEntity.getId();
			for (String s : entry.getValue().getSes()) {
				SeEntity seEntity = new SeEntity();
				seEntity.setHeld(id);
				seEntity.setSe(s);
				seEntity.setAbenteuerId(abenteuerEntity.getId());
				abenteuerEntity.getSes().add(seEntity);
			}
			if(entry.getValue().getAp() != 0) {
				BonusApEntity bonusApEntity = new BonusApEntity();
				bonusApEntity.setAp(entry.getValue().getAp());
				bonusApEntity.setHeld(id);
				bonusApEntity.setAbenteuerId(abenteuerEntity.getId());
				abenteuerEntity.getBonusAp().add(bonusApEntity);
			}
		}
		abenteuerRepository.save(abenteuerEntity);
	}

	@DeleteMapping("{id}")
	public void deleteById(@PathVariable Integer id) {
		AbenteuerEntity entity = abenteuerRepository.findById(id).get();
		entity.getSes().clear();
		entity.getBonusAp().clear();
		abenteuerRepository.save(entity);
		abenteuerRepository.delete(entity);
	}

	@GetMapping("gruppe/{gruppeid}")
	public List<AbenteuerDto> findBygruppeId(@PathVariable Integer gruppeid) {
		return abenteuerRepository.findByGruppeId(gruppeid)
				.stream()
				.map(abenteuerEntity ->  {
					AbenteuerDto dto = new AbenteuerDto();
					dto.setName(abenteuerEntity.getName());
					AbenteuerDto.Bonus bonusAll = new AbenteuerDto.Bonus();
					bonusAll.setAp(abenteuerEntity.getAp());
					Map<String, AbenteuerDto.Bonus> map = new HashMap<>();
					for (BonusApEntity bonusApEntity : abenteuerEntity.getBonusAp()) {
						HeldEntity heldEntity = heldRepository.findById(bonusApEntity.getHeld()).get();
						map.putIfAbsent(heldEntity.getName(), new AbenteuerDto.Bonus());
						AbenteuerDto.Bonus bonus = map.get(heldEntity.getName());
						bonus.setAp(bonusApEntity.getAp());
					}
					for (SeEntity seEntity : abenteuerEntity.getSes()) {
						if(seEntity.getHeld().equals(NEGATIVE)) {
							bonusAll.getSes().add(seEntity.getSe());
						} else {
							HeldEntity heldEntity = heldRepository.findById(seEntity.getHeld()).get();
							map.putIfAbsent(heldEntity.getName(), new AbenteuerDto.Bonus());
							AbenteuerDto.Bonus bonus = map.get(heldEntity.getName());
							bonus.getSes().add(seEntity.getSe());
						}


					}
					dto.setBonus(map);
					dto.setBonusAll(bonusAll);
					dto.setId(abenteuerEntity.getId());
					return dto;
				}).collect(Collectors.toList());
	}
}
