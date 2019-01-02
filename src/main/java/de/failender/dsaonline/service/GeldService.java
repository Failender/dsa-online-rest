package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.exceptions.NoWritePermissionException;
import de.failender.dsaonline.exceptions.NotEnoughMoneyException;
import de.failender.dsaonline.rest.dto.AddWaehrungDto;
import de.failender.dsaonline.rest.dto.DropdownData;
import de.failender.dsaonline.rest.dto.HeldWithVersion;
import de.failender.dsaonline.rest.dto.Währung;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.util.XmlUtil;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import de.failender.heldensoftware.xml.datenxml.Münze;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeldService {


	private final HeldRepositoryService heldRepositoryService;
	private final HeldenApi heldenApi;
	private final List<DropdownData> waehrungenDropdown;
	private final SecurityUtils securityUtils;
	private final HeldenService heldenService;
	private final UserRepository userRepository;



	public GeldService(HeldRepositoryService heldRepositoryService, HeldenApi heldenApi, SecurityUtils securityUtils, HeldenService heldenService, UserRepository userRepository) {
		this.heldRepositoryService = heldRepositoryService;
		this.heldenApi = heldenApi;
		this.securityUtils = securityUtils;
		this.heldenService = heldenService;
		this.userRepository = userRepository;

		waehrungenDropdown = Arrays.stream(Währung.values())
				.map(data -> new DropdownData(data.name(),data.name()))
				.collect(Collectors.toList());
	}

	public List<Münze> getMünzen(BigInteger heldid) {
		HeldWithVersion heldWithVersion = heldRepositoryService.findHeldWithLatestVersion(heldid);
		return heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(heldid, null, heldWithVersion.getVersion().getCacheId())).block().getMünzen().getMünze();
	}

	public List<DropdownData> getWaehrungen(){
		return waehrungenDropdown;
	}

	public List<Münze> add(BigInteger heldid, AddWaehrungDto dto) {
		HeldWithVersion heldWithVersion = heldRepositoryService.findHeldWithLatestVersion(heldid);
		securityUtils.canCurrentUserEditHeld(heldWithVersion.getHeld());
		UserEntity userEntity = userRepository.findById(heldWithVersion.getHeld().getUserId()).get();
		if(!userEntity.isCanWrite()) {
			throw new NoWritePermissionException();
		}
		String xml = heldenApi.request(new ReturnHeldXmlRequest(heldid, null, heldWithVersion.getVersion().getCacheId()))
				.block();
		Element held = XmlUtil.getHeldFromXml(xml);
		Element geldboerse = (Element) held.getElementsByTagName("geldboerse").item(0);

		List<Münze> münzen = new ArrayList<>();
		NodeList nodeList = geldboerse.getElementsByTagName("muenze");
		for(int i = 0; i< nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(!(node instanceof Element) ) {
				continue;
			}
			Element mElement = (Element) node;
			Münze münze = new Münze();
			münze.setAnzahl(new BigInteger(mElement.getAttribute("anzahl")));
			münze.setName(mElement.getAttribute("name"));
			münzen.add(münze);
		}
		int totalMoney = 0;
		Währung dtoWährung = Währung.valueOf(dto.getWaehrung());
		Währung base = dtoWährung.getBaseWährung();
		for (Münze münze : münzen) {
			Währung währung = Währung.valueOf(münze.getName());
			if(währung.getBaseWährung() == base) {
				totalMoney += währung.getValue() * münze.getAnzahl().intValue();
			}
		}
		if(dto.isAdd()) {
			totalMoney += dto.getAmount() * dtoWährung.getValue();
		} else {
			totalMoney -= dto.getAmount() * dtoWährung.getValue();
			if(totalMoney < 0 ) {
				throw new NotEnoughMoneyException();
			}
		}

		Währung währung = base;
		while(totalMoney != 0) {
			// Final variable for lambda
			final Währung wwährung = währung;
			Münze münze = münzen
					.stream()
					.filter(data -> data.getName().equals(wwährung.name()))
					.findFirst()
					.orElseGet(() -> {
						Münze m = new Münze();
						m.setAnzahl(BigInteger.ZERO);
						m.setName(wwährung.name());
						münzen.add(m);

						Element element = geldboerse.getOwnerDocument().createElement("muenze");
						element.setAttribute("name", wwährung.name());
						element.setAttribute("waehrung", wwährung.getWährungName());
						geldboerse.appendChild(element);

						return m;
					});
			if(wwährung.getNext() == null) {
				münze.setAnzahl(BigInteger.valueOf(totalMoney));
				totalMoney = 0;
			} else{
				münze.setAnzahl(BigInteger.valueOf(totalMoney % 10));
				totalMoney /= 10;
				währung = währung.getNext();
			}
			Element element = XmlUtil.findIn(nodeList, (e) -> e.getAttribute("name").equals(wwährung.name()));
			element.setAttribute("anzahl", münze.getAnzahl().toString());
		}

		Element ereignisse = (Element) held.getElementsByTagName("ereignisse").item(0);
		Element ereignis = ereignisse.getOwnerDocument().createElement("ereignis");
		ereignis.setAttribute("text", "Geld");
		ereignis.setAttribute("obj", dto.getName());
		ereignis.setAttribute("datum", String.valueOf(System.currentTimeMillis()));
		ereignis.setAttribute("Alt", "");
		ereignis.setAttribute("Info", "Geldbörse");
		String neu = dto.isAdd() ? "+": "-";
		neu += dto.getAmount();
		neu += " ";
		neu += dto.getWaehrung();
		ereignis.setAttribute("Neu", neu);
		ereignisse.appendChild(ereignis);

		heldenService.lockHeld(heldWithVersion.getHeld());
		xml = XmlUtil.toString(geldboerse.getOwnerDocument());

		heldenService.updateHeldXml(heldWithVersion, xml, userEntity);

		heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(heldid, new TokenAuthentication(userEntity.getToken()), heldWithVersion.getVersion().getCacheId()), false)
				.block();



		return getMünzen(heldid);
	}

}
