// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.rest.dto;

import de.failender.dsaonline.data.entity.EventEntity;
import java.math.BigInteger;

public class CreateEventDto {
	private String name;
	private int date;
	private EventEntity.Type type;
	private BigInteger ownerId;


	public CreateEventDto() {
	}


	public String getName() {
		return this.name;
	}


	public int getDate() {
		return this.date;
	}


	public EventEntity.Type getType() {
		return this.type;
	}


	public BigInteger getOwnerId() {
		return this.ownerId;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public void setDate(final int date) {
		this.date = date;
	}


	public void setType(final EventEntity.Type type) {
		this.type = type;
	}


	public void setOwnerId(final BigInteger ownerId) {
		this.ownerId = ownerId;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof CreateEventDto)) return false;
		final CreateEventDto other = (CreateEventDto) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$name = this.getName();
		final Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		if (this.getDate() != other.getDate()) return false;
		final Object this$type = this.getType();
		final Object other$type = other.getType();
		if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
		final Object this$ownerId = this.getOwnerId();
		final Object other$ownerId = other.getOwnerId();
		if (this$ownerId == null ? other$ownerId != null : !this$ownerId.equals(other$ownerId)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof CreateEventDto;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		result = result * PRIME + this.getDate();
		final Object $type = this.getType();
		result = result * PRIME + ($type == null ? 43 : $type.hashCode());
		final Object $ownerId = this.getOwnerId();
		result = result * PRIME + ($ownerId == null ? 43 : $ownerId.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "CreateEventDto(name=" + this.getName() + ", date=" + this.getDate() + ", type=" + this.getType() + ", ownerId=" + this.getOwnerId() + ")";
	}
}