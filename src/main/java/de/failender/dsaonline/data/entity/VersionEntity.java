package de.failender.dsaonline.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Table(name = "HELD_VERSION")
@Entity
@Data
public class VersionEntity {

	@EmbeddedId
	private VersionId id;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "LAST_EVENT")
	private String lastEvent;

	@Embeddable
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VersionId implements Serializable {
		private BigInteger heldid;
		private int version;
	}

}
