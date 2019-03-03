package de.failender.dsaonline.restservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ReusableByteArrayStream extends ByteArrayInputStream {
	public ReusableByteArrayStream(byte[] buf) {
		super(buf);
	}

	@Override
	public void close() throws IOException {
		super.reset();
	}
}
