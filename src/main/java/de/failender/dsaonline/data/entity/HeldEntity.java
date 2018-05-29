package de.failender.dsaonline.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "HELDEN")
@Data
public class HeldEntity {

	@Id
	private BigInteger id;
	@Column(name = "USER_ID")
	private Integer userId;
	@Column(name = "NAME")
	private String name;
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "PUBLIC")
	private boolean isPublic;
	@Column(name = "DELETED")
	private boolean deleted;


	@JoinColumn(name = "GRUPPE_ID")
	@ManyToOne
	private GruppeEntity gruppe;

}
