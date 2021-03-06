// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.data.dto;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;

public class HeldWithVersion {
	private HeldEntity held;
	private VersionEntity version;


	public HeldEntity getHeld() {
		return this.held;
	}


	public VersionEntity getVersion() {
		return this.version;
	}


	public void setHeld(final HeldEntity held) {
		this.held = held;
	}


	public void setVersion(final VersionEntity version) {
		this.version = version;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof HeldWithVersion)) return false;
		final HeldWithVersion other = (HeldWithVersion) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$held = this.getHeld();
		final Object other$held = other.getHeld();
		if (this$held == null ? other$held != null : !this$held.equals(other$held)) return false;
		final Object this$version = this.getVersion();
		final Object other$version = other.getVersion();
		if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof HeldWithVersion;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $held = this.getHeld();
		result = result * PRIME + ($held == null ? 43 : $held.hashCode());
		final Object $version = this.getVersion();
		result = result * PRIME + ($version == null ? 43 : $version.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "HeldWithVersion(held=" + this.getHeld() + ", version=" + this.getVersion() + ")";
	}


	public HeldWithVersion(final HeldEntity held, final VersionEntity version) {
		this.held = held;
		this.version = version;
	}
}
