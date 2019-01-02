package de.failender.heldensoftware.xml.currentrights;

import javax.xml.bind.annotation.XmlElement;

public class Recht {

	@XmlElement(required = true)
	private String name;
	@XmlElement(required = true)
	private boolean granted;


	public boolean getGranted() {
		return granted;
	}

	public String getName() {
		return name;
	}
}
