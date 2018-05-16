package de.failender.dsaonline.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name="HELDEN")
@Data
public class HeldEntity {

	@EmbeddedId
	private HeldEntityId id;

	private Integer userId;
	private String name;
	private Date createdDate;

	@JoinColumn(name="gruppeId")
	@ManyToOne
	private GruppeEntity gruppe;

	private boolean active;

	@Embeddable
	@Data
	public static class HeldEntityId implements Serializable {
		private BigInteger id;
		private int version;
	}

	@JsonIgnore
	public int getVersion() {
		return id.getVersion();
	}

	@JsonIgnore
	public void setVersion(int version) {
		id.setVersion(version);
	}

	public HeldEntity clone() {
		HeldEntity heldEntity = new HeldEntity();
		heldEntity.setId(new HeldEntityId());
		heldEntity.getId().setId(id.getId());
		heldEntity.getId().setVersion(id.getVersion());
		heldEntity.setActive(active);
		heldEntity.setUserId(userId);
		heldEntity.setCreatedDate(createdDate);
		heldEntity.setGruppe(gruppe);
		heldEntity.setName(name);
		return heldEntity;
	}
}
