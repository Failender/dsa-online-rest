package de.failender.dsaonline.util;

import de.failender.dsaonline.service.ConvertingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
					if(!outFile.exists()) {
						String string = convertingService.convert(file);
						if(string.isEmpty()){
							log.info("Converted is empty for file {}", file.getAbsoluteFile());
						}
						try {
							FileUtils.writeStringToFile(outFile, string, "UTF-8");
						} catch (IOException e) {
							log.error("Critical error converting file {}", file.getAbsoluteFile(), e);
						}
					}
				});
		log.info("Done converting");
	}
}
