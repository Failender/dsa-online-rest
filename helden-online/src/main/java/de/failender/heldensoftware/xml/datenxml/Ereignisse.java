package de.failender.heldensoftware.xml.datenxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"ereignis"
})
@XmlRootElement(name = "ereignisse")
public class Ereignisse {

	protected List<Ereignis> ereignis;


	public List<Ereignis>  getEreignis() {
		if (ereignis == null) {
			ereignis= new ArrayList<>();
		}
		return this.ereignis;
	}

}
