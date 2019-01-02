// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Table(name = "HELD_VERSION")
@Entity
public class VersionEntity extends BaseEntity {
	@Column(name = "HELDID")
	private BigInteger heldid;
	@Column(name = "VERSION")
	private int version;
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "LAST_EVENT")
	private String lastEvent;
	@Column(name = "CACHE_ID")
	private UUID cacheId;
	@Column(name = "AP")
	private Integer ap;

	public VersionEntity() {
	}

	public BigInteger getHeldid() {
		return this.heldid;
	}

	public int getVersion() {
		return this.version;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public String getLastEvent() {
		return this.lastEvent;
	}

	public UUID getCacheId() {
		return this.cacheId;
	}

	public Integer getAp() {
		return this.ap;
	}

	public void setHeldid(final BigInteger heldid) {
		this.heldid = heldid;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setLastEvent(final String lastEvent) {
		this.lastEvent = lastEvent;
	}

	public void setCacheId(final UUID cacheId) {
		this.cacheId = cacheId;
	}

	public void setAp(final Integer ap) {
		this.ap = ap;
	}
}
