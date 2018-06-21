package de.failender.dsaonline.data.entity.abenteuer;

import de.failender.dsaonline.data.entity.AuditingEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.util.List;

@Data
public class AbenteuerEntity extends AuditingEntity {

	private String name;
	private int ap;
	@Column(name = "GRUPPE_ID")
	private Integer gruppeId;

	@JoinColumn(name = "ABENTEUER_ID")
	private List<BonusApEntity> bonusAp;

	@JoinColumn(name = "ABENTEUER_ID")
	private List<SeEntity> ses;


}
