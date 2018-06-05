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

	@Column(name = "START_DATE")
	private int startDate;
	@Column(name = "END_DATE")
	private int endDate;

	@Column(name = "START_MONAT")
	private int startMonat;

	@Column(name ="END_MONAT")
	private int endMonat;

	public enum Type {
		GRUPPE, HELD
	}
}
