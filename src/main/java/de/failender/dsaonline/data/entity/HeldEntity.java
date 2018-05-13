package de.failender.dsaonline.data.entity;

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

	@JoinColumn(name="gruppeId")
	@ManyToOne
	private GruppeEntity gruppe;

	private Integer version;

	private boolean active;

	@Embeddable
	@Data
	public static class HeldEntityId implements Serializable {
		private BigInteger id;
		private Date createdDate;
	}
}
