package de.failender.dsaonline.rest.helden;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeldWithVersion {
	private HeldEntity held;
	private VersionEntity version;
}
