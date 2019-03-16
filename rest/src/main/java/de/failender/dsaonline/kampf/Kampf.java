package de.failender.dsaonline.kampf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.failender.dsaonline.security.SecurityUtils;
import one.util.streamex.EntryStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Kampf {



	private int gegnerId = 0;

	private float scale = 1;
	private int gruppe;
	private int id;
	private String image;
	private List<KampfComponent> components = new ArrayList<>();

	private final Map<Integer, Gegner> gegner = new ConcurrentHashMap<>();

	public Kampf() {

	}

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

	public void setGegner(List<Gegner> gegner) {
		gegner.forEach(this::addGegner);
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

	public void setId(int id) {
		this.id = id;
	}

	public void setGruppe(int gruppe) {
		this.gruppe = gruppe;
	}

	public List<KampfComponent> getComponents() {
		return components;
	}

	public void setComponents(List<KampfComponent> components) {
		this.components = components;
	}

	public void addComponent(KampfComponent kampfComponent) {
		this.components.add(kampfComponent);
	}

	@JsonIgnore
	public KampfComponent getComponent(int id) {
		return this.components.stream().filter(entry -> entry.getId() == id).findFirst().get();
	}
}
