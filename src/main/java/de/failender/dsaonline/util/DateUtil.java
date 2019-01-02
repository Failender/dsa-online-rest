package de.failender.dsaonline.util;

import java.math.BigInteger;
import java.util.Date;

public class DateUtil {

	public static Date convert(BigInteger timestamp) {
		return new Date(timestamp.longValue());
	}
}
