package de.failender.dsaonline.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="USERS")
@Getter
@Setter
public class UserEntity extends AuditingEntity{

	@Column(name = "NAME")
	private String name;
	@Column(name = "PASSWORD")
	private String password;
	@Column(name = "TOKEN")
	private String token;

	@JoinColumn(name="GRUPPE_ID")
	@ManyToOne
	private GruppeEntity gruppe;
}
