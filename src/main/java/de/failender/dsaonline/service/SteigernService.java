package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.exceptions.NoWritePermissionException;
import de.failender.dsaonline.rest.dto.ChangeLernmethodeDto;
import de.failender.dsaonline.rest.dto.HeldWithVersion;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.util.XmlUtil;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.*;
import de.failender.heldensoftware.xml.datenxml.Ap;
import de.failender.heldensoftware.xml.listtalente.SteigerungsTalent;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.math.BigInteger;
import java.util.List;

@Service
public class SteigernService {


	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SteigernService.class);

	private final HeldRepositoryService heldRepositoryService;
	private final SecurityUtils securityUtils;
	private final UserRepository userRepository;
	private final HeldenApi heldenApi;
	private final HeldenService heldenService;

	public SteigernService(HeldRepositoryService heldRepositoryService, SecurityUtils securityUtils, UserRepository userRepository, HeldenApi heldenApi, HeldenService heldenService) {
		this.heldRepositoryService = heldRepositoryService;
		this.securityUtils = securityUtils;
		this.userRepository = userRepository;
		this.heldenApi = heldenApi;
		this.heldenService = heldenService;
	}

	public List<SteigerungsTalent> changeLernmethode(BigInteger heldid, ChangeLernmethodeDto dto) {
		HeldWithVersion heldEntity = heldRepositoryService.findHeldWithLatestVersion(heldid);
		securityUtils.canCurrentUserEditHeld(heldEntity.getHeld());
		heldenService.lockHeld(heldEntity.getHeld());
		UserEntity userEntity = userRepository.findById(heldEntity.getHeld().getUserId()).get();
		if(!userEntity.isCanWrite()) {
			throw new NoWritePermissionException();
		}
		String xml = heldenApi.request(new ReturnHeldXmlRequest(heldid, new TokenAuthentication(userEntity.getToken()), heldEntity.getVersion().getCacheId()))
				.block();
		Element held = XmlUtil.getHeldFromXml(xml);
		Element talentliste = (Element) held.getElementsByTagName("talentliste").item(0);
		for(int i = 0; i<talentliste.getChildNodes().getLength(); i++) {
			Node node = talentliste.getChildNodes().item(i);
			if(!(node instanceof Element)) {
				continue;
			}
			Element e = (Element) node;
			if(e.getAttribute("name").equals(dto.getTalent())) {
				e.setAttribute("lernmethode", dto.getLernmethode());
				xml = XmlUtil.toString(e.getOwnerDocument());
				heldenApi.request(new UpdateXmlRequest(new TokenAuthentication(userEntity.getToken()), xml))
						.block();
				break;
			}
		}
		return getSteigerungen(heldid);
	}

	public List<SteigerungsTalent> getSteigerungen(BigInteger heldid) {
		HeldEntity heldEntity =heldRepositoryService.findHeld(heldid);
		String token = userRepository.findById(heldEntity.getUserId()).get().getToken();
		return heldenApi.request(new ListTalenteRequest(new TokenAuthentication(token), heldid))
				.block().getTalent();
	}

	public List<SteigerungsTalent> steigern(BigInteger heldid, String talent, int aktwert) {
		HeldWithVersion heldWithVersion = heldRepositoryService.findHeldWithLatestVersion(heldid);
		securityUtils.canCurrentUserEditHeld(heldWithVersion.getHeld());
		UserEntity userEntity = userRepository.findById(heldWithVersion.getHeld().getUserId()).get();
		heldenService.lockHeld(heldWithVersion.getHeld());
		heldenApi.request(new RaiseTalentRequest(new TokenAuthentication(userEntity.getToken()), heldid, talent, aktwert))
				.block();
		return getSteigerungen(heldid);
	}

	public Ap getApUncached(BigInteger heldid) {
		HeldEntity heldEntity = heldRepositoryService.findHeld(heldid);
		UserEntity userEntity = userRepository.findById(heldEntity.getUserId()).get();
		return heldenApi.request(new ReturnHeldDatenRequest(heldid, new TokenAuthentication(userEntity.getToken()), null, true))
				.block().getAngaben().getAp();
	}



	public Ap addEreignis(BigInteger heldid, String name, int ap) {
		HeldEntity heldEntity = heldRepositoryService.findHeld(heldid);
		securityUtils.canCurrentUserEditHeld(heldEntity);
		UserEntity userEntity = userRepository.findById(heldEntity.getUserId()).get();

		String xml = heldenApi.request(new ReturnHeldXmlRequest(heldid, new TokenAuthentication(userEntity.getToken()), null, true))
				.block();
		Element held = XmlUtil.getHeldFromXml(xml);
		Element basis = (Element) held.getElementsByTagName("basis").item(0);
		Element abenteuerpunkte = (Element) basis.getElementsByTagName("abenteuerpunkte").item(0);
		int abenteuerpunkteInt = Integer.parseInt(abenteuerpunkte.getAttribute("value"));
		abenteuerpunkteInt += ap;
		abenteuerpunkte.setAttribute("value", String.valueOf(abenteuerpunkteInt));

		Element freieabenteuerpunkte = (Element) basis.getElementsByTagName("freieabenteuerpunkte").item(0);
		int freieabenteuerpunkteInt = Integer.parseInt(freieabenteuerpunkte.getAttribute("value"));
		freieabenteuerpunkteInt += ap;
		freieabenteuerpunkte.setAttribute("value", String.valueOf(freieabenteuerpunkteInt));

		Element ereignisse = (Element) held.getElementsByTagName("ereignisse").item(0);
		Element ereignis = ereignisse.getOwnerDocument().createElement("ereignis");
		ereignis.setAttribute("Abenteuerpunkte", String.valueOf(ap));
		ereignis.setAttribute("kommentar", name + "Gesamt AP: " + ap + " Verfügbare AP: " + ap);
		ereignis.setAttribute("obj", "Abenteuerpunkte (Hinzugewinn)");
		ereignis.setAttribute("text", "Ereignis eingeben");
		ereignis.setAttribute("time", String.valueOf(System.currentTimeMillis()));
		ereignis.setAttribute("version", "HS 5.5.4");
		ereignisse.appendChild(ereignis);

		xml = XmlUtil.toString(ereignisse.getOwnerDocument());
		heldenApi.request(new UpdateXmlRequest(new TokenAuthentication(userEntity.getToken()), xml))
				.block();
		log.info("Updated held xml {} {}", heldEntity.getName(), heldEntity.getId());

		return getApUncached(heldid);
	}
}
