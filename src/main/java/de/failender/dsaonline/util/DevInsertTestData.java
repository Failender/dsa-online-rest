package de.failender.dsaonline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.CachingService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")
public class DevInsertTestData implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHeldenService userHeldenService;

	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private ApiService apiService;

	@Autowired
	private CachingService cachingService;


	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		List<GrantedAuthority> fakeRights = new ArrayList<>();
		fakeRights.add(new SimpleGrantedAuthority(SecurityUtils.CREATE_USER));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(userRepository.findById(1).get(), null, fakeRights));
		InputStream is = DevInsertTestData.class.getClassLoader().getResourceAsStream("user.json");
		ObjectMapper om = new ObjectMapper();
		try {
			List<UserData> data = om.readValue(is, new TypeReference<List<UserData>>(){});
			data.forEach(
					userData -> {
						if(userData.getToken() == null) {
							System.out.println("Token is null. WTF");
						}
						String gruppe = null;
						if(userData.getGruppe() != null) {
							gruppe = userData.getGruppe();
						} else {
							gruppe = this.gruppeRepository.findAll().get(0).getName();
						}
						UserRegistration userRegistration = new UserRegistration(userData.getName(), null, userData.getToken(), gruppe);
						UserEntity userEntity = this.userService.registerUser(userRegistration);
						userData.roles.forEach(role -> {
							this.userService.addUserRole(userEntity, role);
						});
					}
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fakeNextVersion(heldRepository.findFirstByIdIdOrderByIdVersionDesc(BigInteger.valueOf(36222L)).get());



		SecurityContextHolder.getContext().setAuthentication(null);
	}

	private void fakeNextVersion(HeldEntity heldEntity) {


		UserEntity userEntity = this.userRepository.findById(heldEntity.getUserId()).get();
		Daten daten = this.apiService.getHeldenDaten(heldEntity.getId().getId(), heldEntity.getVersion(), userEntity.getToken());
		heldEntity.setActive(false);
		this.heldRepository.save(heldEntity);
		heldEntity = heldEntity.clone();
		heldEntity.setVersion(heldEntity.getVersion() + 1);
		heldEntity.setActive(true);
		this.heldRepository.save(heldEntity);
		Ereignis fakeEreignis = new Ereignis();
		fakeEreignis.setBemerkung("New Version");
		fakeEreignis.setDate("22.11.2015 14:00");
		daten.getEreignisse().getEreignis().add(fakeEreignis);
		cachingService.setHeldenDatenCache(heldEntity.getId().getId(), heldEntity.getVersion(), daten);
	}

	@Data
	public static class UserData {
		private String name;
		private String token;
		private List<String> roles;
		private String gruppe;
	}
}
