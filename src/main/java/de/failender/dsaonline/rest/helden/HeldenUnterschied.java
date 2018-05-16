package de.failender.dsaonline.rest.helden;

import de.failender.heldensoftware.xml.datenxml.Ereignis;
import de.failender.heldensoftware.xml.datenxml.Talent;
import de.failender.heldensoftware.xml.datenxml.Vorteil;
import de.failender.heldensoftware.xml.datenxml.Zauber;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeldenUnterschied {
	private Unterschiede<Talent> talente;
	private Unterschiede<Zauber> zauber;
	private Unterschiede<Ereignis> ereignis;
	private Unterschiede<Vorteil> vorteile;

}
