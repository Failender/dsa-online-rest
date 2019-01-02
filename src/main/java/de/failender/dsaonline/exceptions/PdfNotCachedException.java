package de.failender.dsaonline.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigInteger;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PdfNotCachedException extends RuntimeException {
	public PdfNotCachedException(BigInteger heldid, int version) {
		super("Held mit id " + heldid + " und Version " + version + " hat kein pdf");
	}

	public PdfNotCachedException(BigInteger heldid, int version, int page) {
		super("Held mit id " + heldid + " und Version " + version + " und Seite " + page + " existiert nicht");
	}
}
