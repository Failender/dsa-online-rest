package de.failender.heldensoftware.xml.listtalente;

import de.failender.heldensoftware.xml.heldenliste.Held;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"talent"
})
@XmlRootElement(name = "talente")
public class ListTalente {

	protected List<SteigerungsTalent> talent;

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
	public List<SteigerungsTalent> getTalent() {
		if (talent == null) {
			talent = new ArrayList<>();
		}
		return this.talent;
	}

	public void setTalent(List<SteigerungsTalent> talent) {
		this.talent= talent;
	}
}
