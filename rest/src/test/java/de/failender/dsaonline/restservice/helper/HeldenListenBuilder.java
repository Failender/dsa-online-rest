package de.failender.dsaonline.restservice.helper;

import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigInteger;
import java.util.ArrayList;

public class HeldenListenBuilder {
	private Helden helden;

	public static HeldenListenBuilder heldenliste() {
		return new HeldenListenBuilder();
	}

	public HeldenListenBuilder() {
		helden = new Helden();
		helden.setHeld(new ArrayList<>());
	}

	public HeldenListenBuilder held(Held held) {
		helden.getHeld().add(held);
		return this;
	}

	public HeldenListenBuilder held(BigInteger heldenid, String name, BigInteger heldenkey, Long heldlastchange) {
		Held held = new Held();
		held.setHeldenid(heldenid);
		held.setName(name);
		held.setHeldenkey(heldenkey);
		held.setHeldlastchange(BigInteger.valueOf(heldlastchange));
		return held(held);
	}

	public Helden build() {
		return helden;
	}



	@XmlElement(required = true)
	protected BigInteger heldenid;
	@XmlElement(required = true)
	protected String name;
	@XmlElement(required = true)
	protected BigInteger heldenkey;
	@XmlElement(required = true)
	protected BigInteger heldlastchange;
}
