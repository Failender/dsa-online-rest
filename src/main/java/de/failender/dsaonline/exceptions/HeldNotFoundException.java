package de.failender.dsaonline.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigInteger;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HeldNotFoundException extends RuntimeException{
	public HeldNotFoundException(BigInteger heldid, int version) {
		super("Held mit id " + heldid + " und Version " + version +" konnte nicht gefunden werden");
	}
}
