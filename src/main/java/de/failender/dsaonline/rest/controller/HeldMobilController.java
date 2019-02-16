package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.rest.dto.HeldMobilInformation;
import de.failender.dsaonline.service.heldmobil.HeldMobilService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("api/held/mobil/{heldid}")
public class HeldMobilController {

    private final HeldMobilService heldMobilService;

    public HeldMobilController(HeldMobilService heldMobilService) {
        this.heldMobilService = heldMobilService;
    }

    @GetMapping
    public HeldMobilInformation getMobilInformation(@PathVariable BigInteger heldid) {
        return heldMobilService.getMobilInformation(heldid);
    }

}
