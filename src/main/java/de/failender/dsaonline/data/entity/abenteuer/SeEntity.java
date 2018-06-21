package de.failender.dsaonline.data.entity.abenteuer;

import de.failender.dsaonline.data.entity.BaseEntity;

import javax.persistence.Column;
import java.math.BigInteger;

public class SeEntity extends BaseEntity {
	private BigInteger held;
	@Column(name = "ABENTEUER_ID")
	private Integer abenteuerId;
	private String se;
}
