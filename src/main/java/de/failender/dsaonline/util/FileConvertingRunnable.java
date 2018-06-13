package de.failender.dsaonline.util;

import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.ConvertingRequest;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileConvertingRunnable implements Runnable {


	private final HeldenApi heldenApi;
	private final String fakesDirectory;

	public FileConvertingRunnable(HeldenApi heldenApi, String fakesDirectory) {
		this.heldenApi = heldenApi;

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
					log.info("Processing directory {} ", diretory.getName());
					String heldid = diretory.getName();
					File[] files = diretory.listFiles();
					Arrays.sort(files, (a, b) -> {

						try {
							Long aCreation = Files.readAttributes(a.toPath(), BasicFileAttributes.class).creationTime().toMillis();
							Long bCreation = Files.readAttributes(b.toPath(), BasicFileAttributes.class).creationTime().toMillis();
							return aCreation.compareTo(bCreation);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
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
					String fileName = FilenameUtils.removeExtension(file.getName()) + ".zip";
					File outFile = new File(outDir, fileName);
					if (!outFile.exists()) {

						try {
							ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outFile));
							log.info("Converting file: {}", file.getName());
							String xml = FileUtils.readFileToString(file, "UTF-8");
							ConvertingRequest datenRequest = new ConvertingRequest(HeldenApi.Format.datenxml, xml);
							ConvertingRequest pdfRequest = new ConvertingRequest(HeldenApi.Format.pdfintern, xml);
							InputStream stream = heldenApi.request(datenRequest, false);
							//Make sure the file is valid
							Daten daten = (Daten) JaxbUtil.getUnmarshaller(Daten.class).unmarshal(stream);
							UserHeldenService.clearEreigniskontrolle(daten.getEreignisse().getEreignis());
							zos.putNextEntry(new ZipEntry("daten.xml"));
							JaxbUtil.getMarshaller(Daten.class).marshal(daten, zos);
							zos.closeEntry();
							zos.putNextEntry(new ZipEntry("held.pdf"));
							IOUtils.copy(heldenApi.requestRaw(pdfRequest, false), zos);
							zos.closeEntry();
							zos.putNextEntry(new ZipEntry("held.xml"));
							IOUtils.copy(IOUtils.toInputStream(xml, "UTF-8"), zos);
							zos.closeEntry();
							zos.close();

							log.info("Finished converting {}", file.getName());
						} catch (JAXBException e) {
							log.error("Critical error converting file, returned xml is invalid {}", file.getAbsoluteFile());
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
		log.info("Done converting");
	}

	private FilenameFilter getFiles() {
		return (dir, name) -> !new File(dir, name).isDirectory();
	}

	private FilenameFilter getDirectories() {
		return (dir, name) -> new File(dir, name).isDirectory();
	}
}
