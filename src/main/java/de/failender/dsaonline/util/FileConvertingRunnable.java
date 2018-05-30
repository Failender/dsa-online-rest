package de.failender.dsaonline.util;

import de.failender.dsaonline.service.ConvertingService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

@Slf4j
public class FileConvertingRunnable implements Runnable {


	private final ConvertingService convertingService;
	private final String fakesDirectory;

	public FileConvertingRunnable(ConvertingService convertingService, String fakesDirectory) {
		this.convertingService = convertingService;

		this.fakesDirectory = fakesDirectory;
	}

	@Override
	public void run() {
		log.info("Starting FileConvertingRunnable");
		final File outDir = new File(fakesDirectory + "/versionfakes");
		final File dir = new File(fakesDirectory + "/versionfakes_helden");

		log.info("Starting to convert directories to version id format");
		Arrays.stream(dir.listFiles(getDirectories()))
				.forEach(diretory -> {
					log.info("Processing directory {} " , diretory.getName());
					String heldid = diretory.getName();
					File[] files = diretory.listFiles();
					Arrays.sort(files, (a,b) -> {

						try {
							Long aCreation = Files.readAttributes(a.toPath(), BasicFileAttributes.class).creationTime().toMillis();
							Long bCreation = Files.readAttributes(b.toPath(), BasicFileAttributes.class).creationTime().toMillis();
							return aCreation.compareTo(bCreation);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					} );
					for (int i = 0; i < files.length; i++) {
						File out = new File(dir, i + 1 + "." + heldid + ".xml");
						try {
							Files.copy(files[i].toPath(), new FileOutputStream(out));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

				});
		log.info("Starting to convert helden to daten format");
		Arrays.stream(dir.listFiles(getFiles()))
				.forEach(file -> {
					File outFile = new File(outDir, file.getName());
					if (!outFile.exists()) {
						log.info("Converting file: {}", file.getName());
						String string = convertingService.convert(file);

						if (string.isEmpty()) {
							log.info("Converted is empty for file {}", file.getAbsoluteFile());
							throw new RuntimeException("Converted is empty for file");
						}
						try {
							//Make sure the file is valid
							Daten daten = (Daten) JaxbUtil.getUnmarshaller(Daten.class).unmarshal(new StringReader(string));
							UserHeldenService.clearEreigniskontrolle(daten.getEreignisse().getEreignis());
							JaxbUtil.getMarshaller(Daten.class).marshal(daten, outFile);
							log.info("Finished converting {}", file.getName());
						} catch (JAXBException e) {
							log.error("Critical error converting file, returned xml is invalid {}", file.getAbsoluteFile());
							throw new RuntimeException(e);
						}
					}
				});
		log.info("Done converting");
	}

	private FilenameFilter getFiles() {
		return (dir, name) -> !new File(dir,name).isDirectory();
	}

	private FilenameFilter getDirectories() {
		return (dir, name) -> new File(dir,name).isDirectory();
	}
}
