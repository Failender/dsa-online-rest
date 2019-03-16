package de.failender.dsaonline.kampf;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.failender.dsaonline.kampf.updates.UpdateGegnerPosition;
import de.failender.dsaonline.security.SecurityUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class KampfService {


	private final SimpMessagingTemplate template;
	private int nextId;

	private final Cache<Integer, Kampf> kampfCache = Caffeine.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.build();

	public KampfService(SimpMessagingTemplate template) {
		this.template = template;
	}


	public Kampf startKampf(int gruppe, Kampf kampf) {
		SecurityUtils.INSTANCE.checkIsUserMeisterForGruppe(gruppe);
		int id = nextId ++;

		Kampf old = getKampf(gruppe);
		if(old != null) {
			this.kampfCache.invalidate(old.getId());
		}
		kampf.setId(id);
		kampf.setGruppe(gruppe);

//
//		kampf.addGegner(new Gegner(Gegner.ICON_AXE, 50, 50, 30, 30, true));
//		kampf.addGegner(new Gegner(Gegner.ICON_BOW, 100, 50, 23, 30, true));
//		kampf.addGegner(new Gegner(Gegner.ICON_MAGE, 150, 50, 15, 30, false));
//		kampf.addGegner(new Gegner(Gegner.ICON_MAGE, 150, 100, 8, 30, false));
//		kampf.addGegner(new Gegner(Gegner.ICON_DUALSWORD, 200, 50, 7, 30, false));

		this.template.convertAndSend("/kampf/gruppe/" + gruppe, kampf );
		kampfCache.put(id, kampf);

		return kampf;
	}

	public Kampf getKampfForGruppe(int gruppe) {
		for (Map.Entry<Integer, Kampf> integerKampfEntry : kampfCache.asMap().entrySet()) {
			if(integerKampfEntry.getValue().getGruppe() == gruppe) {
				return integerKampfEntry.getValue();
			}
		}
		return null;
	}

	private Kampf getKampf(int kampfid) {
		Kampf kampf = kampfCache.getIfPresent(kampfid);
		if(kampf == null) {
			return null;
		}
		SecurityUtils.INSTANCE.checkIsUserMeisterForGruppe(kampf.getGruppe());
		return kampf;
	}

	public void updateGegner(int kampfid, Gegner gegner) {

		Kampf kampf = getKampf(kampfid);
		Gegner kampfGegner = kampf.getGegnerById(gegner.getId());
		kampfGegner.setX(gegner.getX());
		kampfGegner.setY(gegner.getY());

		this.template.convertAndSend("/kampf/" + kampfid + "/teilnehmer/position", new UpdateGegnerPosition(gegner.getId(), gegner.getX(), gegner.getY()));

	}

	public void updateImage(int kampfid, String image) {
		Kampf kampf = getKampf(kampfid);
		kampf.setImage(image);
		this.template.convertAndSend("/kampf/" + kampfid + "/image", image);

	}

	public void addComponent(int kampfid, KampfComponent component) {
		Kampf kampf = getKampf(kampfid);
		kampf.addComponent(component);
		this.template.convertAndSend("/kampf/" + kampfid + "/component/add", component);

	}

	public void updateComponent(int kampfid, KampfComponent component) {
		Kampf kampf = getKampf(kampfid);
		KampfComponent our = kampf.getComponent(component.getId());
		our.setX(component.getX());
		our.setY(component.getY());
		this.template.convertAndSend("/kampf/" + kampfid + "/component/update", our);

	}
}
