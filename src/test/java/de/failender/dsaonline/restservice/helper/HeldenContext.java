package de.failender.dsaonline.restservice.helper;

import lombok.Data;

import java.math.BigInteger;

@Data
public class HeldenContext {
	private BigInteger heldid;
	private String name;
	private long gesamtAp;
	private long stand;
	private String lastEreignis;
	private int lastEreignisAp;
}
