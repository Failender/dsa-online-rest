package de.failender.dsaonline.data.entity.abenteuer;

import de.failender.dsaonline.data.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigInteger;

@Data
public class BonusApEntity extends BaseEntity {
	private BigInteger held;
	@Column(name="ABENTEUER_ID")
	private Integer abenteuerId;
	private Integer ap;
}
