// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.data.entity;


import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "HELDEN")
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
	@Column(name = "ACTIVE")
	private boolean isActive = true;
	@Column(name = "DELETED")
	private boolean deleted;
	@JoinColumn(name = "GRUPPE_ID")
	@ManyToOne
	private GruppeEntity gruppe;

	@Column(name = "HKEY")
	private Long key;
	@Column(name = "LOCK_EXPIRE")
	private Date lockExpire;

	public HeldEntity() {
	}

	public BigInteger getId() {
		return this.id;
	}


	public Integer getUserId() {
		return this.userId;
	}


	public String getName() {
		return this.name;
	}


	public Date getCreatedDate() {
		return this.createdDate;
	}


	public boolean isPublic() {
		return this.isPublic;
	}


	public boolean isActive() {
		return this.isActive;
	}


	public boolean isDeleted() {
		return this.deleted;
	}


	public GruppeEntity getGruppe() {
		return this.gruppe;
	}


	public void setId(final BigInteger id) {
		this.id = id;
	}


	public void setUserId(final Integer userId) {
		this.userId = userId;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}


	public void setPublic(final boolean isPublic) {
		this.isPublic = isPublic;
	}


	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}


	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}


	public void setGruppe(final GruppeEntity gruppe) {
		this.gruppe = gruppe;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof HeldEntity)) return false;
		final HeldEntity other = (HeldEntity) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$id = this.getId();
		final Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final Object this$userId = this.getUserId();
		final Object other$userId = other.getUserId();
		if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
		final Object this$name = this.getName();
		final Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final Object this$createdDate = this.getCreatedDate();
		final Object other$createdDate = other.getCreatedDate();
		if (this$createdDate == null ? other$createdDate != null : !this$createdDate.equals(other$createdDate)) return false;
		if (this.isPublic() != other.isPublic()) return false;
		if (this.isActive() != other.isActive()) return false;
		if (this.isDeleted() != other.isDeleted()) return false;
		final Object this$gruppe = this.getGruppe();
		final Object other$gruppe = other.getGruppe();
		if (this$gruppe == null ? other$gruppe != null : !this$gruppe.equals(other$gruppe)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof HeldEntity;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final Object $userId = this.getUserId();
		result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
		final Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final Object $createdDate = this.getCreatedDate();
		result = result * PRIME + ($createdDate == null ? 43 : $createdDate.hashCode());
		result = result * PRIME + (this.isPublic() ? 79 : 97);
		result = result * PRIME + (this.isActive() ? 79 : 97);
		result = result * PRIME + (this.isDeleted() ? 79 : 97);
		final Object $gruppe = this.getGruppe();
		result = result * PRIME + ($gruppe == null ? 43 : $gruppe.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "HeldEntity(id=" + this.getId() + ", userId=" + this.getUserId() + ", name=" + this.getName() + ", createdDate=" + this.getCreatedDate() + ", isPublic=" + this.isPublic() + ", isActive=" + this.isActive() + ", deleted=" + this.isDeleted() + ", gruppe=" + this.getGruppe() + ")";
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Date getLockExpire() {
		return lockExpire;
	}

	public void setLockExpire(Date lockExpire) {
		this.lockExpire = lockExpire;
	}
}