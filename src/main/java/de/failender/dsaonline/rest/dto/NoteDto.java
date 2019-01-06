// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.rest.dto;

public class NoteDto {
	private int id;
	private String note;


	public int getId() {
		return this.id;
	}


	public String getNote() {
		return this.note;
	}


	public void setId(final int id) {
		this.id = id;
	}


	public void setNote(final String note) {
		this.note = note;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof NoteDto)) return false;
		final NoteDto other = (NoteDto) o;
		if (!other.canEqual((Object) this)) return false;
		if (this.getId() != other.getId()) return false;
		final Object this$note = this.getNote();
		final Object other$note = other.getNote();
		if (this$note == null ? other$note != null : !this$note.equals(other$note)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof NoteDto;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.getId();
		final Object $note = this.getNote();
		result = result * PRIME + ($note == null ? 43 : $note.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "NoteDto(id=" + this.getId() + ", note=" + this.getNote() + ")";
	}


	public NoteDto(final int id, final String note) {
		this.id = id;
		this.note = note;
	}
}