package de.failender.dsaonline.rest.abenteuer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AbenteuerDto {

	private int id;
	private int gruppe;
	private String name;
	private Bonus bonusAll;
	private Map<String, Bonus> bonus;

	@Data
	public static class Bonus {
		private int ap;
		private List<String> ses = new ArrayList<>();

	}
}
