package de.failender.dsaonline.rest.helden;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Unterschiede<T> {

	private List<T> neu = new ArrayList<>();
	private List<T> entfernt = new ArrayList<>();
	private List<Unterschied> aenderungen = new ArrayList<>();


	public void addNeu(T value) {
		neu.add(value);
	}


	public void addEntfernt(T value) {
		entfernt.add(value);
	}

	public void addAenderung(Unterschied value) {
		aenderungen.add(value);
	}



}
