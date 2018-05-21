package de.failender.dsaonline.service;

import de.failender.heldensoftware.Helper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class ConvertingService {

	public String convert(File file) {
		try {
			return this.convert(IOUtils.toString(new FileReader(file)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String convert(String string) {
		try {
			return Helper.postrequesturl("https://online.helden-software.de/converter/?output=datenxml", "held", string);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
