package de.failender.dsaonline.rest.helden;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class HeldVersion {
	private String letztesAbenteuer;
	private Date datum;
	private int version;
	private boolean pdfCached;
}
