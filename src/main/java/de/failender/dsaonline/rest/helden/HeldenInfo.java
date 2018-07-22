package de.failender.dsaonline.rest.helden;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
public class HeldenInfo {
	private String name;
	private Date lastChanged;
	private int version;
	private String gruppe;
	private BigInteger id;
	private boolean oeffentlich;
	private boolean active;
}
