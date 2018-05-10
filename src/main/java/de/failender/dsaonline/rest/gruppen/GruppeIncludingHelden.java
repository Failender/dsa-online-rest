package de.failender.dsaonline.rest.gruppen;

import de.failender.dsaonline.rest.helden.HeldenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GruppeIncludingHelden {
	private String name;
	private int id;
	private List<HeldenInfo> helden;
}
