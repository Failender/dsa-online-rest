package de.failender.dsaonline.data.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="SCRIPT_VARIABLE")
@Data
public class ScriptVariableEntity extends BaseEntity{


	@Column(name="SCRIPT_ID")
	private Integer scriptId;
	@Column(name="NAME")
	private String name;
	@Column(name="TYPE")
	private String type;
	@Column(name="VALUE")
	private String value;

}
