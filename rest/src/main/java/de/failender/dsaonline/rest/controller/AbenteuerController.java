package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.rest.dto.AbenteuerDto;
import de.failender.dsaonline.rest.dto.CreateBonus;
import de.failender.dsaonline.service.AbenteuerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/abenteuer")
public class AbenteuerController {

	private final AbenteuerService abenteuerService;

	public AbenteuerController(AbenteuerService abenteuerService) {
		this.abenteuerService = abenteuerService;
	}


	@PostMapping("{gruppe}/{kampagne}/{name}/{ap}/{datum}")
	public ResponseEntity<?> createAbenteuer(@PathVariable int gruppe, @PathVariable int kampagne, @PathVariable String name, @PathVariable int ap, @PathVariable int datum) {

		if(abenteuerService.createAbenteuer(gruppe, kampagne, name, ap, datum)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@DeleteMapping("{id}")
	public void deleteById(@PathVariable Integer id) {
		abenteuerService.deleteById(id);
	}

	@GetMapping("gruppe/{gruppeid}")
	public List<AbenteuerDto> findBygruppeId(@PathVariable Integer gruppeid) {
		return abenteuerService.findByGruppeId(gruppeid);
	}

	@GetMapping("kampagne/{kampagneid}")
	public List<AbenteuerDto> findAbenteuerByKampagne(@PathVariable int kampagneid) {
		return abenteuerService.findAbenteuerByKampagne(kampagneid);
	}

	@GetMapping("{id}")
	public AbenteuerDto findAbenteuer(@PathVariable int id) {
		return abenteuerService.findAbenteuerById(id);
	}

	@PostMapping("bonus")
	public void saveBonus(@RequestBody CreateBonus bonus) {
		abenteuerService.saveBonus(bonus);
	}

	@DeleteMapping("{abenteuerid}/bonus/{heldid}")
	public void deleteBonus(@PathVariable int abenteuerid, @PathVariable BigInteger heldid) {
		abenteuerService.deleteBonus(abenteuerid, heldid);
	}

	@PostMapping("ap/{abenteuer}/{held}/{ap}")
	public void createApBonus(@PathVariable int abenteuer,@PathVariable BigInteger held, @PathVariable int ap) {
		abenteuerService.createApBonus(abenteuer, held, ap);
	}

	@DeleteMapping("{abenteuerid}/bonus/ap/{heldid}")
	public void deleteApBonus(@PathVariable int abenteuerid, @PathVariable BigInteger heldid) {
		abenteuerService.deleteApBonus(abenteuerid, heldid);
	}

	@PostMapping("se/{abenteuer}/{held}/{se}")
	public void createSeBonus(@PathVariable int abenteuer, @PathVariable BigInteger held, @PathVariable String se) {
		abenteuerService.createSeBonus(abenteuer, held, se);
	}

	@DeleteMapping("{abenteuerid}/bonus/se/{heldid}/{name}")
	public void deleteSeBonus(@PathVariable int abenteuerid, @PathVariable String name, @PathVariable BigInteger heldid) {
		abenteuerService.deleteSeBonus(abenteuerid, heldid, name);
	}

	@PostMapping("lm/{abenteuer}/{held}/{lm}")
	public void createLmBonus(@PathVariable int abenteuer, @PathVariable BigInteger held, @PathVariable String lm) {
		abenteuerService.createLmBonus(abenteuer, held, lm);
	}

	@DeleteMapping("{abenteuerid}/bonus/lm/{heldid}/{name}")
	public void deleteLmBonus(@PathVariable int abenteuerid, @PathVariable String name, @PathVariable BigInteger heldid) {
		abenteuerService.deleteLmBonus(abenteuerid, heldid, name);
	}

	@PostMapping("note/{abenteuer}")
	public void createNoteBonus(@PathVariable int abenteuer, @RequestBody String note) {
		abenteuerService.createNoteBonus(abenteuer,  note);
	}

	@DeleteMapping("{abenteuerid}/bonus/note/{id}")
	public void deleteNoteBonus(@PathVariable int abenteuerid, @PathVariable int id) {
		abenteuerService.deleteNoteBonus(abenteuerid, id);
	}

	@PostMapping("{abenteuerid}/name/{name}")
	public void editName(@PathVariable int abenteuerid, @PathVariable String name) {
		abenteuerService.editName(abenteuerid, name);
	}

	@PostMapping("{abenteuerid}/date/{date}")
	public void editDate(@PathVariable int abenteuerid, @PathVariable int date) {
		abenteuerService.editDate(abenteuerid, date);
	}














}
