package de.failender.heldensoftware.xml.currentrights;

import de.failender.heldensoftware.xml.heldenliste.Held;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"recht"
})
@XmlRootElement(name = "rechte")
public class Rechte {

	protected List<Recht> recht;

	/**
	 * Gets the value of the xmlHeld property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the xmlHeld property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getHeld().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Held }
	 *
	 *
	 */
	public List<Recht> getRecht() {
		if (recht == null) {
			recht = new ArrayList<>();
		}
		return this.recht;
	}

	public void setRecht(List<Recht> recht) {
		this.recht= recht;
	}
}
