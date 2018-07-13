package de.failender.dsaonline.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

@Table(name = "HELD_VERSION")
@Entity
@Data
public class VersionEntity extends BaseEntity{

	@Column(name = "HELDID")
	private BigInteger heldid;
	@Column(name = "VERSION")
	private int version;
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "LAST_EVENT")
	private String lastEvent;

	@Column(name ="AP")
	private Integer ap;

}
