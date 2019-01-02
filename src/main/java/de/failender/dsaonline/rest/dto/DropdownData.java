// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.rest.dto;

public class DropdownData {
	private String label;
	private Object value;


	public String getLabel() {
		return this.label;
	}


	public Object getValue() {
		return this.value;
	}


	public void setLabel(final String label) {
		this.label = label;
	}


	public void setValue(final Object value) {
		this.value = value;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof DropdownData)) return false;
		final DropdownData other = (DropdownData) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$label = this.getLabel();
		final Object other$label = other.getLabel();
		if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
		final Object this$value = this.getValue();
		final Object other$value = other.getValue();
		if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof DropdownData;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $label = this.getLabel();
		result = result * PRIME + ($label == null ? 43 : $label.hashCode());
		final Object $value = this.getValue();
		result = result * PRIME + ($value == null ? 43 : $value.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "DropdownData(label=" + this.getLabel() + ", value=" + this.getValue() + ")";
	}


	public DropdownData(final String label, final Object value) {
		this.label = label;
		this.value = value;
	}
}
