package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.rest.dto.ChangeLernmethodeDto;
import de.failender.dsaonline.service.SteigernService;
import de.failender.heldensoftware.xml.datenxml.Ap;
import de.failender.heldensoftware.xml.listtalente.SteigerungsTalent;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/steigern/{heldid}")
public class SteigernController {

    private final SteigernService steigernService;

    public SteigernController(SteigernService steigernService) {
        this.steigernService = steigernService;
    }

    @PostMapping("steigern/lernmethode")
    public List<SteigerungsTalent> changeLernmethode(@PathVariable BigInteger heldid, @RequestBody ChangeLernmethodeDto dto) {
        return steigernService.changeLernmethode(heldid, dto);
    }

    @GetMapping("steigerungen")
    public List<SteigerungsTalent> getSteigerungen(@PathVariable BigInteger heldid) {
        return steigernService.getSteigerungen(heldid);
    }

    @PostMapping("steigern/{talent}/{aktwert}")
    public List<SteigerungsTalent> steigern(@PathVariable BigInteger heldid, @PathVariable String talent, @PathVariable int aktwert ) {
        return steigernService.steigern(heldid, talent, aktwert);
    }

    @GetMapping("ap")
    public Ap getApUncached(@PathVariable BigInteger heldid) {
        return steigernService.getApUncached(heldid);
    }

    @PostMapping("ereignis/{name}/{ap}")
    public Ap addEreignis(@PathVariable BigInteger heldid, @PathVariable String name, @PathVariable int ap) {
        return this.steigernService.addEreignis(heldid, name, ap);
    }
}
