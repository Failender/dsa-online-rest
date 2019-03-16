package de.failender.dsaonline.kampf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.failender.dsaonline.security.SecurityUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Kampf {



	private int gegnerId = 0;

	private float scale = 1;
	private final int gruppe;
	private final int id;
	private String image;

	private final Map<Integer, Gegner> gegner = new ConcurrentHashMap<>();

	public Kampf(int gruppe, int id, String image) {
		this.gruppe = gruppe;
		this.id = id;
		this.image = image;
	}

	public void addGegner(Gegner gegner) {
		gegner.setId(gegnerId ++);
		this.gegner.put(gegner.getId(), gegner);
	}

	public int getGruppe() {
		return gruppe;
	}

	@JsonIgnore
	public Gegner getGegnerById(int id) {
		return gegner.get(id);
	}

	public Collection<Gegner> getGegner() {
		return gegner.values();
	}

	public boolean isReadonly() {
		return !SecurityUtils.INSTANCE.checkIsUserMeisterForGruppeBool(gruppe);
	}

	public int getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
