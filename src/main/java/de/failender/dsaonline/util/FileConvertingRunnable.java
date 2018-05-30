package de.failender.dsaonline.util;

import de.failender.dsaonline.service.ConvertingService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

@Slf4j
public class FileConvertingRunnable implements Runnable {


	private final ConvertingService convertingService;

	public FileConvertingRunnable(ConvertingService convertingService) {
		this.convertingService = convertingService;
	}

	@Override
	public void run() {
		log.info("Starting FileConvertingRunnable");
		final File outDir = new File("fakes/versionfakes");
		final File dir = new File("fakes/versionfakes_helden");

		Arrays.stream(dir.listFiles()).parallel()
				.forEach(file -> {
					File outFile = new File(outDir, file.getName());
					if (!outFile.exists()) {
						log.info("Converting file: {}", file.getName());
						String string = convertingService.convert(file);

						if (string.isEmpty()) {
							log.info("Converted is empty for file {}", file.getAbsoluteFile());
						}
						try {
							//Make sure the file is valid
							JaxbUtil.getUnmarshaller(Daten.class).unmarshal(new StringReader(string));
							FileUtils.writeStringToFile(outFile, string, "UTF-8");
							log.info("Finished converting {}", file.getName());
						} catch (IOException e) {
							log.error("Critical error converting file {}", file.getAbsoluteFile(), e);
						} catch (JAXBException e) {
							log.error("Critical error converting file, returned xml is invalid {}", file.getAbsoluteFile());
						}
					}
				});
		log.info("Done converting");
	}
}
