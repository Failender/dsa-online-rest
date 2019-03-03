package de.failender.dsaonline.rest.dto;


public enum Währung {

	Kreuzer("Mittelreich"), Heller("Mittelreich"), Silbertaler("Mittelreich"), Dukat("Mittelreich");

	private String währungName;
	private Währung baseWährung;
	private Währung prev;
	private Währung next;
	private int value = 1;

	Währung(String währungName) {
		this.währungName = währungName;
	}

	static {
		Kreuzer.baseWährung = Kreuzer;
		Heller.baseWährung = Kreuzer;
		Silbertaler.baseWährung = Kreuzer;
		Dukat.baseWährung = Kreuzer;

		Kreuzer.next = Heller;

		Heller.prev = Kreuzer;
		Heller.next = Silbertaler;
		Heller.value = 10;

		Silbertaler.prev = Heller;
		Silbertaler.next = Dukat;
		Silbertaler.value = 100;

		Dukat.prev = Silbertaler;
		Dukat.value = 1000;

	}

	public Währung getBaseWährung() {
		return baseWährung;
	}

	public Währung getPrev() {
		return prev;
	}

	public Währung getNext() {
		return next;
	}

	public int getValue() {
		return value;
	}

	public String getWährungName() {
		return währungName;
	}
}
