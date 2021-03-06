// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.data.entity.abenteuer;

import de.failender.dsaonline.data.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "ABENTEUER_BONUS_AP")
public class BonusApEntity extends BaseEntity {
	private BigInteger held;
	@Column(name = "ABENTEUER_ID")
	private Integer abenteuerId;
	private Integer ap;


	public BonusApEntity() {
	}


	public BigInteger getHeld() {
		return this.held;
	}


	public Integer getAbenteuerId() {
		return this.abenteuerId;
	}


	public Integer getAp() {
		return this.ap;
	}


	public void setHeld(final BigInteger held) {
		this.held = held;
	}


	public void setAbenteuerId(final Integer abenteuerId) {
		this.abenteuerId = abenteuerId;
	}


	public void setAp(final Integer ap) {
		this.ap = ap;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof BonusApEntity)) return false;
		final BonusApEntity other = (BonusApEntity) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$held = this.getHeld();
		final Object other$held = other.getHeld();
		if (this$held == null ? other$held != null : !this$held.equals(other$held)) return false;
		final Object this$abenteuerId = this.getAbenteuerId();
		final Object other$abenteuerId = other.getAbenteuerId();
		if (this$abenteuerId == null ? other$abenteuerId != null : !this$abenteuerId.equals(other$abenteuerId)) return false;
		final Object this$ap = this.getAp();
		final Object other$ap = other.getAp();
		if (this$ap == null ? other$ap != null : !this$ap.equals(other$ap)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof BonusApEntity;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $held = this.getHeld();
		result = result * PRIME + ($held == null ? 43 : $held.hashCode());
		final Object $abenteuerId = this.getAbenteuerId();
		result = result * PRIME + ($abenteuerId == null ? 43 : $abenteuerId.hashCode());
		final Object $ap = this.getAp();
		result = result * PRIME + ($ap == null ? 43 : $ap.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "BonusApEntity(held=" + this.getHeld() + ", abenteuerId=" + this.getAbenteuerId() + ", ap=" + this.getAp() + ")";
	}
}
