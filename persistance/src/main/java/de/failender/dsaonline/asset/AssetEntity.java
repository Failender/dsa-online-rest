package de.failender.dsaonline.asset;

import de.failender.dsaonline.data.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Entity
@Table(name = "ASSET")
public class AssetEntity extends BaseEntity {

	private Integer kampagne;
	private String name;
	private boolean hidden;

	public Integer getKampagne() {
		return kampagne;
	}

	public void setKampagne(Integer kampagne) {
		this.kampagne = kampagne;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Transactional
	public String getFilename() {
		return kampagne + "-" + name;
	}
}
