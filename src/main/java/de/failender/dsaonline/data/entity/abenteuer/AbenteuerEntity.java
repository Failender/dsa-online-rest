package de.failender.dsaonline.data.entity.abenteuer;

import de.failender.dsaonline.data.entity.AuditingEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name ="ABENTEUER")
@Data
public class AbenteuerEntity extends AuditingEntity {

	private String name;
	private int ap;
	@Column(name = "GRUPPE_ID")
	private Integer gruppeId;

	@JoinColumn(name = "ABENTEUER_ID")
	@OneToMany
	private List<BonusApEntity> bonusAp;

	@JoinColumn(name = "ABENTEUER_ID")
	@OneToMany
	private List<SeEntity> ses;


}
