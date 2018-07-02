package de.failender.dsaonline.data.entity;

import de.failender.dsaonline.data.entity.abenteuer.BonusApEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name ="SCRIPTS")
@Data
public class ScriptEntity extends BaseEntity{


	private String name;
	@JoinColumn(name = "SCRIPT_ID")
	@OneToMany(cascade = CascadeType.ALL)
	private List<ScriptVariable> scriptVariables;

	private String body;

}
