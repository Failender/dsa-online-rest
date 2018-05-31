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

	//Data-Type might change once frontend knows what it needs
	@Column(name = "START_DATE")
	private String startDate;
	@Column(name = "END_DATE")
	private String endDate;


	public enum Type {
		GRUPPE, HELD
	}
}
