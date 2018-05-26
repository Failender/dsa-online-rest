package de.failender.dsaonline.data.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditingEntity extends BaseEntity{

	@CreatedDate
	@Column(name = "CREATED_DATE")
	private Date createdDate;
}