package de.failender.dsaonline.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="GRUPPEN")
@Getter
@Setter
public class GruppeEntity extends BaseEntity {
	private String name;
}
