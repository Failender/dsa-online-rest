package de.failender.dsaonline.rest.helden;

import de.failender.heldensoftware.xml.datenxml.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeldenUnterschied {
	private Unterschiede<Talent> talente;
	private Unterschiede<Zauber> zauber;
	private Unterschiede<Ereignis> ereignis;
	private Unterschiede<Vorteil> vorteile;
	private Unterschiede<Gegenstand> gegenstaende;

}
