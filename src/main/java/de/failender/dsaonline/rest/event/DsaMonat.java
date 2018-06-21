package de.failender.dsaonline.rest.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DsaMonat {

	public static final int YEAR_LENGTH = 365;
	public static final int MONTH_LENGTH = 30;
	public static final int NAMENLOS_MONTH_LENGTH = 30;
	public static final int START_YEAR = 1000;
	public static final int NAMENLOS_MONTH = 12;


	private int jahr;
	private int monat;

	public int getDbJahr() {
		return jahr - START_YEAR;
	}

	public int getDbMonat() {
		return getDbJahr() * 13 + monat;
	}

	public int getStartTag() {

		return getDbJahr() * 365 + monat * 30;

	}

	public int getEndTag() {
		if (monat == NAMENLOS_MONTH) {
			return getDbJahr() * 365 + monat * 30 + NAMENLOS_MONTH_LENGTH - 1;
		} else {
			return getDbJahr() * 365 + monat * 30 + MONTH_LENGTH - 1;
		}
	}
}
