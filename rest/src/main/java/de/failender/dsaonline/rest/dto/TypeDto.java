// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.rest.dto;

import de.failender.dsaonline.util.SelectData;
import java.util.List;

public class TypeDto {
	private String name;
	private String description;
	private List<SelectData> values;


	public TypeDto(final String name, final String description, final List<SelectData> values) {
		this.name = name;
		this.description = description;
		this.values = values;
	}


	public String getName() {
		return this.name;
	}


	public String getDescription() {
		return this.description;
	}


	public List<SelectData> getValues() {
		return this.values;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public void setDescription(final String description) {
		this.description = description;
	}


	public void setValues(final List<SelectData> values) {
		this.values = values;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof TypeDto)) return false;
		final TypeDto other = (TypeDto) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$name = this.getName();
		final Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final Object this$description = this.getDescription();
		final Object other$description = other.getDescription();
		if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
		final Object this$values = this.getValues();
		final Object other$values = other.getValues();
		if (this$values == null ? other$values != null : !this$values.equals(other$values)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof TypeDto;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final Object $description = this.getDescription();
		result = result * PRIME + ($description == null ? 43 : $description.hashCode());
		final Object $values = this.getValues();
		result = result * PRIME + ($values == null ? 43 : $values.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "TypeDto(name=" + this.getName() + ", description=" + this.getDescription() + ", values=" + this.getValues() + ")";
	}
}
