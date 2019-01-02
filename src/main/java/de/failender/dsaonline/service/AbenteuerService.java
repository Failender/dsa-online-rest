// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.KampagneEntity;
import de.failender.dsaonline.data.entity.abenteuer.*;
import de.failender.dsaonline.data.repository.AbenteuerRepository;
import de.failender.dsaonline.data.service.GruppeRepositoryService;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.data.service.KampagneRepositoryService;
import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import de.failender.dsaonline.rest.dto.*;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbenteuerService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbenteuerService.class);
	@Autowired
	private AbenteuerRepository abenteuerRepository;
	private final GruppeRepositoryService gruppeRepositoryService;
	private final KampagneRepositoryService kampagneRepositoryService;
	private final SecurityUtils securityUtils;
	public static final BigInteger NEGATIVE = BigInteger.valueOf(-1);
	@Autowired
	private HeldRepositoryService heldRepositoryService;

	public AbenteuerService(GruppeRepositoryService gruppeRepositoryService, KampagneRepositoryService kampagneRepositoryService, SecurityUtils securityUtils) {
		this.gruppeRepositoryService = gruppeRepositoryService;
		this.kampagneRepositoryService = kampagneRepositoryService;
		this.securityUtils = securityUtils;
	}

	public boolean createAbenteuer(int gruppe, int kampagne, String name, int ap, int datum) {
		securityUtils.checkIsUserMeisterForGruppeBool(gruppe);
		kampagneRepositoryService.findKampagneById(kampagne);
		if (abenteuerRepository.existsByGruppeIdAndNameAndKampagne(gruppe, name, kampagne)) {
			log.info("Skipping abenteuer-creation since a duplicate for the group {} kampagne {} with name {} already exists", gruppe, kampagne, name);
			return false;
		}
		AbenteuerEntity abenteuerEntity = new AbenteuerEntity();
		abenteuerEntity.setName(name);
		abenteuerEntity.setGruppeId(gruppe);
		abenteuerEntity.setKampagne(kampagne);
		abenteuerEntity.setDatum(datum);
		abenteuerEntity.setAp(ap);
		abenteuerRepository.save(abenteuerEntity);
		return true;
	}

	public void deleteById(Integer id) {
		AbenteuerEntity entity = abenteuerRepository.findById(id).get();
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		securityUtils.checkIsUserMeisterForGruppe(entity.getGruppeId());
		entity.getSes().clear();
		entity.getBonusAp().clear();
		abenteuerRepository.save(entity);
		abenteuerRepository.delete(entity);
	}

	public List<AbenteuerDto> findByGruppeId(Integer gruppeid) {
		return abenteuerRepository.findByGruppeIdOrderByDatumDesc(gruppeid).stream().map(this::toDto).collect(Collectors.toList());
	}

	public AbenteuerDto findAbenteuerById(int id) {
		return toDto(abenteuerRepository.findById(id).get());
	}

	public AbenteuerDto toDto(AbenteuerEntity entity) {
		KampagneEntity kampagneEntity = kampagneRepositoryService.findKampagneById(entity.getKampagne());
		AbenteuerDto dto = new AbenteuerDto();
		dto.setId(entity.getId());
		dto.setKampagneId(kampagneEntity.getId());
		dto.setKampagne(kampagneEntity.getName());
		dto.setName(entity.getName());
		GruppeEntity gruppeEntity = gruppeRepositoryService.findById(entity.getGruppeId());
		dto.setGruppe(gruppeEntity.getName());
		dto.setGruppeId(gruppeEntity.getId());
		dto.setMeister(securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId()));
		dto.setDatumValue(entity.getDatum());
		dto.getBonusAll().setAp(entity.getAp());
		dto.setAp(entity.getAp());
		dto.setNotes(entity.getNotes().stream().map(note -> new NoteDto(note.getId(), note.getNote())).collect(Collectors.toList()));
		for (SeEntity seEntity : entity.getSes()) {
			if (seEntity.getHeld().equals(NEGATIVE)) {
				dto.getBonusAll().getSes().add(seEntity.getSe());
			} else {
				try {
					Bonus bonus = findBonus(dto.getBonus(), seEntity.getHeld());
					bonus.getSes().add(seEntity.getSe());
				} catch (AccessDeniedException | NotAuthenticatedException e) {
					;
				}
			}
		}
		for (LmEntity lmEntity : entity.getLms()) {
			if (lmEntity.getHeld().equals(NEGATIVE)) {
				dto.getBonusAll().getLms().add(lmEntity.getLm());
			} else {
				try {
					Bonus bonus = findBonus(dto.getBonus(), lmEntity.getHeld());
					bonus.getLms().add(lmEntity.getLm());
				} catch (AccessDeniedException | NotAuthenticatedException e) {
					;
				}
			}
		}
		for (NoteEntity noteEntity : entity.getNotes()) {
			if (noteEntity.getHeld().equals(NEGATIVE)) {
				dto.getBonusAll().getNotes().add(noteEntity.getNote());
			} else {
				try {
					Bonus bonus = findBonus(dto.getBonus(), noteEntity.getHeld());
					bonus.getNotes().add(noteEntity.getNote());
				} catch (AccessDeniedException | NotAuthenticatedException e) {
					;
				}
			}
		}
		for (BonusApEntity bonusApEntity : entity.getBonusAp()) {
			try {
				Bonus bonus = findBonus(dto.getBonus(), bonusApEntity.getHeld());
				bonus.setAp(bonusApEntity.getAp());
			} catch (AccessDeniedException | NotAuthenticatedException e) {
				;
			}
		}
		return dto;
	}

	private Bonus findBonus(List<HeldenBonus> heldenBonuses, BigInteger id) {
		HeldEntity heldEntity = heldRepositoryService.findHeld(id);
		Optional<HeldenBonus> bonus = heldenBonuses.stream().filter(value -> value.getName().equals(heldEntity.getName())).findFirst();
		if (bonus.isPresent()) {
			return bonus.get().getBonus();
		} else {
			HeldenBonus heldenBonus = new HeldenBonus(heldEntity.getName(), heldEntity.getId());
			heldenBonuses.add(heldenBonus);
			return heldenBonus.getBonus();
		}
	}

	public void saveBonus(CreateBonus createBonus) {
		AbenteuerEntity entity = abenteuerRepository.findById(createBonus.getAbenteuerId()).get();
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		if (!createBonus.getHeldid().equals(NEGATIVE)) {
			heldRepositoryService.findHeld(createBonus.getHeldid());
		}
		Bonus bonus = createBonus.getBonus();
		if (bonus.getAp() != 0) {
			BonusApEntity bonusApEntity = new BonusApEntity();
			bonusApEntity.setAp(bonus.getAp());
			bonusApEntity.setHeld(createBonus.getHeldid());
			bonusApEntity.setAbenteuerId(createBonus.getAbenteuerId());
		}
		if (!bonus.getSes().isEmpty()) {
			for (String s : bonus.getSes()) {
				SeEntity seEntity = new SeEntity();
				seEntity.setAbenteuerId(createBonus.getAbenteuerId());
				seEntity.setHeld(createBonus.getHeldid());
				seEntity.setSe(s);
			}
		}
	}

	public List<AbenteuerDto> findAbenteuerByKampagne(int kampagneid) {
		return abenteuerRepository.findByKampagneOrderByDatumDesc(kampagneid).stream().map(this::toDto).collect(Collectors.toList());
	}

	public void createApBonus(int abenteuer, BigInteger held, int ap) {
		AbenteuerEntity abenteuerEntity = validateExistence(abenteuer, held);
		securityUtils.checkIsUserMeisterForGruppeBool(abenteuerEntity.getGruppeId());
		if (held.equals(NEGATIVE)) {
			abenteuerEntity.setAp(ap);
			abenteuerRepository.save(abenteuerEntity);
			return;
		}
		Iterator<BonusApEntity> it = abenteuerEntity.getBonusAp().iterator();
		while (it.hasNext()) {
			BonusApEntity entity = it.next();
			if (entity.getHeld().equals(held)) {
				it.remove();
			}
		}
		if (held.equals(NEGATIVE)) {
			abenteuerEntity.setAp(ap);
			return;
		}
		BonusApEntity bonusApEntity = new BonusApEntity();
		bonusApEntity.setAbenteuerId(abenteuer);
		bonusApEntity.setHeld(held);
		bonusApEntity.setAp(ap);
		abenteuerEntity.getBonusAp().add(bonusApEntity);
		abenteuerRepository.save(abenteuerEntity);
	}

	public void createSeBonus(int abenteuer, BigInteger held, String se) {
		AbenteuerEntity entity = validateExistence(abenteuer, held);
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		SeEntity seEntity = new SeEntity();
		seEntity.setHeld(held);
		seEntity.setAbenteuerId(abenteuer);
		seEntity.setSe(se);
		entity.getSes().add(seEntity);
		abenteuerRepository.save(entity);
	}

	private AbenteuerEntity validateExistence(int abenteuer, BigInteger held) {
		if (!held.equals(NEGATIVE)) {
			heldRepositoryService.findHeld(held);
		}
		return abenteuerRepository.findById(abenteuer).get();
	}

	public void deleteBonus(int abenteuer, BigInteger heldid) {
		AbenteuerEntity abenteuerEntity = abenteuerRepository.findById(abenteuer).get();
		securityUtils.checkIsUserMeisterForGruppeBool(abenteuerEntity.getGruppeId());
		if (heldid.equals(NEGATIVE)) {
			abenteuerEntity.setAp(0);
			Iterator<SeEntity> seEntityIterator = abenteuerEntity.getSes().iterator();
			while (seEntityIterator.hasNext()) {
				SeEntity seEntity = seEntityIterator.next();
				if (seEntity.getHeld().equals(NEGATIVE)) {
					seEntityIterator.remove();
				}
			}
			abenteuerRepository.save(abenteuerEntity);
			return;
		}
		Iterator<SeEntity> seEntityIterator = abenteuerEntity.getSes().iterator();
		while (seEntityIterator.hasNext()) {
			SeEntity seEntity = seEntityIterator.next();
			if (seEntity.getHeld().equals(heldid)) {
				seEntityIterator.remove();
			}
		}
		Iterator<BonusApEntity> bonusApIterator = abenteuerEntity.getBonusAp().iterator();
		while (bonusApIterator.hasNext()) {
			BonusApEntity bonusApEntity = bonusApIterator.next();
			if (bonusApEntity.getHeld().equals(heldid)) {
				bonusApIterator.remove();
			}
		}
		abenteuerRepository.save(abenteuerEntity);
	}

	public void deleteSeBonus(int abenteuer, BigInteger heldid, String name) {
		AbenteuerEntity abenteuerEntity = abenteuerRepository.findById(abenteuer).get();
		securityUtils.checkIsUserMeisterForGruppeBool(abenteuerEntity.getGruppeId());
		if (heldid.equals(NEGATIVE)) {
			Iterator<SeEntity> seEntityIterator = abenteuerEntity.getSes().iterator();
			while (seEntityIterator.hasNext()) {
				SeEntity seEntity = seEntityIterator.next();
				if (seEntity.getHeld().equals(NEGATIVE) && seEntity.getSe().equals(name)) {
					seEntityIterator.remove();
					break;
				}
			}
			abenteuerRepository.save(abenteuerEntity);
			return;
		}
		Iterator<SeEntity> seEntityIterator = abenteuerEntity.getSes().iterator();
		while (seEntityIterator.hasNext()) {
			SeEntity seEntity = seEntityIterator.next();
			if (seEntity.getHeld().equals(heldid) && seEntity.getSe().equals(name)) {
				seEntityIterator.remove();
				break;
			}
		}
		abenteuerRepository.save(abenteuerEntity);
	}

	public void deleteLmBonus(int abenteuer, BigInteger heldid, String name) {
		AbenteuerEntity abenteuerEntity = abenteuerRepository.findById(abenteuer).get();
		securityUtils.checkIsUserMeisterForGruppeBool(abenteuerEntity.getGruppeId());
		if (heldid.equals(NEGATIVE)) {
			Iterator<LmEntity> lmEntityIterator = abenteuerEntity.getLms().iterator();
			while (lmEntityIterator.hasNext()) {
				LmEntity lmEntity = lmEntityIterator.next();
				if (lmEntity.getHeld().equals(NEGATIVE) && lmEntity.getLm().equals(name)) {
					lmEntityIterator.remove();
					break;
				}
			}
			abenteuerRepository.save(abenteuerEntity);
			return;
		}
		Iterator<LmEntity> lmEntityIterator = abenteuerEntity.getLms().iterator();
		while (lmEntityIterator.hasNext()) {
			LmEntity lmEntity = lmEntityIterator.next();
			if (lmEntity.getHeld().equals(heldid) && lmEntity.getLm().equals(name)) {
				lmEntityIterator.remove();
				break;
			}
		}
		abenteuerRepository.save(abenteuerEntity);
	}

	public void deleteApBonus(int abenteuer, BigInteger heldid) {
		AbenteuerEntity abenteuerEntity = abenteuerRepository.findById(abenteuer).get();
		securityUtils.checkIsUserMeisterForGruppeBool(abenteuerEntity.getGruppeId());
		if (heldid.equals(NEGATIVE)) {
			abenteuerEntity.setAp(0);
			abenteuerRepository.save(abenteuerEntity);
			return;
		}
		Iterator<BonusApEntity> bonusApIterator = abenteuerEntity.getBonusAp().iterator();
		while (bonusApIterator.hasNext()) {
			BonusApEntity bonusApEntity = bonusApIterator.next();
			if (bonusApEntity.getHeld().equals(heldid)) {
				bonusApIterator.remove();
			}
		}
		abenteuerRepository.save(abenteuerEntity);
	}

	public void createLmBonus(int abenteuer, BigInteger held, String lm) {
		AbenteuerEntity entity = validateExistence(abenteuer, held);
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		LmEntity lmEntity = new LmEntity();
		lmEntity.setHeld(held);
		lmEntity.setAbenteuerId(abenteuer);
		lmEntity.setLm(lm);
		entity.getLms().add(lmEntity);
		abenteuerRepository.save(entity);
	}

	public void createNoteBonus(int abenteuer, String note) {
		AbenteuerEntity entity = validateExistence(abenteuer, NEGATIVE);
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		NoteEntity noteEntity = new NoteEntity();
		noteEntity.setHeld(NEGATIVE);
		noteEntity.setAbenteuerId(abenteuer);
		noteEntity.setNote(note);
		entity.getNotes().add(noteEntity);
		abenteuerRepository.save(entity);
	}

	public void deleteNoteBonus(int abenteuerid, int id) {
		AbenteuerEntity entity = abenteuerRepository.findById(abenteuerid).get();
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		entity.getNotes().remove(entity.getNotes().stream().filter(note -> note.getId() == id).findFirst().get());
		abenteuerRepository.save(entity);
	}

    public void editName(int abenteuerid, String name) {
		AbenteuerEntity entity = abenteuerRepository.findById(abenteuerid).get();
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		entity.setName(name);
		abenteuerRepository.save(entity);
	}

	public void editDate(int abenteuerid, int date) {
		AbenteuerEntity entity = abenteuerRepository.findById(abenteuerid).get();
		securityUtils.checkIsUserMeisterForGruppeBool(entity.getGruppeId());
		entity.setDatum(date);
		abenteuerRepository.save(entity);
	}
}
