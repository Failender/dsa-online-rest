package de.failender.dsaonline.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="USERS")
@Getter
@Setter
public class UserEntity extends BaseEntity{
	private String name;
	private String password;
	private String token;

	@JoinColumn(name="GRUPPE_ID")
	@ManyToOne
	private GruppeEntity gruppe;
}
