package de.failender.dsaonline.service;

import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.exceptions.PdfNotCachedException;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
public class CachingService {

	@Value("${dsa.online.cache.directory}")
	private String cacheDirectoryString;

	@Value("${dsa.online.cache.duration}")
	private int cacheDuration;

	private File xmlCacheDirectory;
	private File datenCacheDirectory;
	private File pdfCacheDirectory;

	@PostConstruct
	public void init() {

		xmlCacheDirectory = new File(cacheDirectoryString + "/xml");
		if (!xmlCacheDirectory.exists()) {
			log.info("Creating caching-directory for xml");
			xmlCacheDirectory.mkdirs();
		} else {
			log.info("caching-directory for xml already exists");
		}
		datenCacheDirectory = new File(cacheDirectoryString + "/daten");
		if (!datenCacheDirectory.exists()) {
			log.info("Creating caching-directory for daten");
			datenCacheDirectory.mkdirs();
		} else {
			log.info("caching-directory for daten already exists");
		}

		pdfCacheDirectory = new File(cacheDirectoryString + "/pdf");
		if (!pdfCacheDirectory.exists()) {
			log.info("Creating caching-directory for pdf");
			pdfCacheDirectory.mkdirs();
		} else {
			log.info("caching-directory for pdf already exists");
		}
	}

	public Daten getHeldenDatenCache(BigInteger heldid, int version) {
		File file = getHeldenDatenCacheFile(heldid, version);
		if (file.exists()) {
			long age = System.currentTimeMillis() - file.lastModified();
			if (age > cacheDuration) {
				return null;
			}
			log.info("Found cache for heldendaten {}.{}", version, heldid);
			return JaxbUtil.datenFromFile(file);
		}
		return null;
	}

	public List<Held> getAllHeldenCache(String token) {
		File file = getAllHeldenCacheFile(token);
		if (file.exists()) {

			long age = System.currentTimeMillis() - file.lastModified();
			if (age > cacheDuration) {
				return null;
			}
			log.info("Found cache for allhelden with token {}", token);
			return JaxbUtil.heldenFromFile(file);
		}
		return null;
	}

	public void setAllHeldenCache(String token, List<Held> helden) {

		Helden xmlHelden = new Helden();
		xmlHelden.setHeld(helden);
		Marshaller marshaller = JaxbUtil.getMarshaller(Helden.class);
		try {
			marshaller.marshal(xmlHelden, getAllHeldenCacheFile(token));
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}

	}

	public void purgeAllHeldenCache(String token) {
		File file = getAllHeldenCacheFile(token);
		if (file.exists()) {
			if (!file.delete()) {
				log.error("Failed to purgeAllHeldenCache for token " + token);
			}
		}
	}

	public void purgePdfCacheFor(BigInteger id, int version) {
		File file = getHeldenPdfCacheFile(id, version);
		if (file.exists()) {
			file.delete();
		}
		File dir = getHeldenPdfCacheDirectory(id, version);
		if (dir.exists()) {
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				log.error("Error while deleting pdf directory", e);
			}
		}

	}

	public void setHeldenCache(BigInteger heldid, int version, Daten daten, String xml) {
		setHeldenDatenCache(heldid, version, daten);
		setHeldenXmlCache(heldid, version, xml);
	}

	private void setHeldenDatenCache(BigInteger heldid, int version, Daten daten) {
		Marshaller marshaller = JaxbUtil.getMarshaller(Daten.class);
		try {
			marshaller.marshal(daten, getHeldenDatenCacheFile(heldid, version));
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	public boolean hasPdfCache(BigInteger heldid, int version) {
		return getHeldenPdfCacheFile(heldid, version).exists();
	}

	public void setHeldenPdfCache(BigInteger heldid, int version, InputStream inputStreamReader) {
		File out = getHeldenPdfCacheFile(heldid, version);
		try {
			FileUtils.copyInputStreamToFile(inputStreamReader, out);
			log.info("Wrote pdf cache for held {} version {}", heldid, version);


			PDDocument document = PDDocument.load(out);
			Splitter splitter = new Splitter();
			List<PDDocument> docs = splitter.split(document);
			File directory = new File(out.getParentFile(), FilenameUtils.removeExtension(out.getName()));
			directory.mkdir();
			for (int i = 0; i < docs.size(); i++) {
				PDDocument doc = docs.get(i);
				File file = new File(directory, i + 1 + ".pdf");
				doc.save(file);
				doc.close();
			}
			document.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public File getPdfCache(BigInteger heldid, int version) {
		return getHeldenPdfCacheFile(heldid, version);
	}

	private void setHeldenXmlCache(BigInteger heldid, int version, String xml) {
		try {
			FileUtils.writeStringToFile(getHeldenXmlCacheFile(heldid, version), xml, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void dropCache() {
		log.warn("DROPPING CACHE!");
		try {
			FileUtils.cleanDirectory(xmlCacheDirectory);
			FileUtils.cleanDirectory(datenCacheDirectory);
			FileUtils.cleanDirectory(pdfCacheDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getAllHeldenCacheFile(String token) {
		return new File(datenCacheDirectory, token + ".xml");
	}

	private File getHeldenDatenCacheFile(BigInteger id, int version) {
		return new File(datenCacheDirectory, version + "." + id + ".xml");
	}

	private File getHeldenXmlCacheFile(BigInteger id, int version) {
		return new File(xmlCacheDirectory, version + "." + id + ".xml");
	}

	private File getHeldenPdfCacheFile(BigInteger id, int version) {
		return new File(pdfCacheDirectory, version + "." + id + ".pdf");
	}

	private File getHeldenPdfCacheDirectory(BigInteger id, int version) {
		return new File(pdfCacheDirectory, version + "." + id);
	}

	private File getCacheFile(BigInteger id, int version, CacheType type) {
		switch (type) {
			case pdf:
				return getHeldenPdfCacheFile(id, version);
			case daten:
				return getHeldenPdfCacheFile(id, version);
			case xml:
				return getHeldenPdfCacheFile(id, version);
			default:
				throw new IllegalArgumentException("Type " + type + " not found");
		}
	}

	public enum CacheType {
		daten(".xml"), xml(".xml"), pdf(".pdf");
		private String extension;

		CacheType(String extension) {
			this.extension = extension;
		}

		public String getExtension() {
			return extension;
		}
	}

	public void provideDownload(@PathVariable BigInteger heldid, @PathVariable int version, HttpServletResponse response, CacheType type) {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + heldid + "." + version + "." + type.getExtension());
		try {
			File file = getCacheFile(heldid, version, type);
			if (file.exists()) {
				IOUtils.copy(new FileInputStream(getCacheFile(heldid, version, type)), response.getOutputStream());
			} else {
				throw new PdfNotCachedException(heldid, version);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
