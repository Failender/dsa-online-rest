package de.failender.dsaonline.data.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name="HELDEN")
@Data
public class HeldEntity {

	@Id
	private BigInteger id;

	private Integer userId;
	private String name;

	@JoinColumn(name="gruppeId")
	@ManyToOne
	private GruppeEntity gruppe;

	private int version;

	private boolean active;
	private String xml;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
}
