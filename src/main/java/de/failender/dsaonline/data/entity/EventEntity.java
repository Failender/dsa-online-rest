package de.failender.dsaonline.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "EVENTS")
@Data
public class EventEntity extends BaseEntity {


	@Column(name = "NAME")
	private String name;
	@Column(name = "OWNER_ID")
	private BigInteger ownerId;
	@Column(name = "TYPE")
	@Enumerated(EnumType.ORDINAL)
	private Type type;
	@Column(name = "DATE")
	private int date;

	public enum Type {
		GRUPPE, HELD
	}
}
