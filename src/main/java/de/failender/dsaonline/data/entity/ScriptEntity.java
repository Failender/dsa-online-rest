package de.failender.dsaonline.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name ="SCRIPTS")
@Data
public class ScriptEntity extends BaseEntity {

	@Column(name = "NAME")
	private String name;

	@Column(name = "OWNER")
	private Integer owner;

	@Column(name = "BODY")
	private String body;

	@JoinColumn(name = "SCRIPT_ID")
	@OneToMany(cascade = CascadeType.ALL)
	private List<ScriptVariableEntity> scriptVariables;

	@Column(name = "SCRIPT_HELPER")
	private ArrayList<String> scriptHelper;

}