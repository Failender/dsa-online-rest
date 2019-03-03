package de.failender.dsaonline.restservice;

import java.io.IOException;
import java.io.InputStream;

public class NearlyEmptyInputStream extends InputStream {

	private int i = 5;
	@Override
	public int read() throws IOException {
		return i--;
	}

	@Override
	public void close() throws IOException {
		i = 5;
	}
}
