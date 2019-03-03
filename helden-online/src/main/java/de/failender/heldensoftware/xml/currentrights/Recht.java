package de.failender.heldensoftware.xml.currentrights;

import javax.xml.bind.annotation.XmlElement;

public class Recht {


	private String name;

	private boolean granted;

	@XmlElement(required = true)
	public boolean getGranted() {
		return granted;
	}
	@XmlElement(required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}
}
