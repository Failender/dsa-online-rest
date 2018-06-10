package de.failender.heldensoftware;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


@Slf4j
public class Helper {

	static {
		disableSSLCheck();
	}

	public static String postrequest(String ...strings) throws Exception {
		return postrequesturl("https://online.helden-software.de", strings);

	}

	public static String postrequesturl(String url, String ...strings) throws Exception {
		Writer swriter = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader =
				new BufferedReader(postrequeststreamurl(url, strings));
		int count;
		while ((count = reader.read(buffer)) != -1) {
			swriter.write(buffer, 0, count);
		}
		reader.close();
		return swriter.toString();

	}


	 public static InputStreamReader postrequeststream(String ...strings) throws Exception {
		return postrequeststreamurl("https://online.helden-software.de", strings);
	}

	public static InputStreamReader postrequeststreamurl(String adress, String ... strings) throws Exception {
		return new InputStreamReader(postrequeststreamurlnoreaderurl(adress, strings), "UTF-8");
	}

	public static InputStream postrequeststreamurlnoreader(String ... strings) throws Exception {
		return postrequeststreamurlnoreaderurl("https://online.helden-software.de", strings);
	}

	public static InputStream postrequeststreamurlnoreaderurl(String adress, String ... strings) throws Exception {
		String body = buildBody(strings);
		log.info("Sending request: "  + body);
		URL url = new URL(adress);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(body);
		writer.close();

		return connection.getInputStream();
	}

	private static String buildBody(String... strings) throws Exception {
		String body = "";
		for (int i = 0; i < strings.length; i = i + 2) {
			if (!body.isEmpty()) {
				body += "&";
			}
			if(i+1 == strings.length || strings[i+1] == null) {
				System.err.println("Fatal error in building post for param " + strings[i]);
			}
			body += URLEncoder.encode(strings[i], "UTF-8");
			body += "=";
			body += URLEncoder.encode(strings[i + 1], "UTF-8");
		}
		return body;
	}



	/**
	 * Deaktiviert alle SSL Checks
	 * Nur für selbst-signierte Certifikate bei localhost zu nutzen!
	 * Alles andere für zu extremen Sicherheitsproblemen!
	 */
	public static void disableSSLCheck() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			//
		}

	}


	static public NodeList getDaten(Document daten, String search) {

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			//search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(daten, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
			//return getDatenAsString(search);
		}
	}

	/**
	 * Wandelt einen String in ein XML-Dokument um
	 * @param xmlstring XML-String
	 * @return fertiges DOC
	 * @throws SAXException Fehler
	 * @throws IOException Fehler
	 * @throws ParserConfigurationException Fehler
	 */
	public static Document string2Doc(String xmlstring)
			throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory dbf =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlstring));
		return db.parse(is);

	}


	static public String getDatenAsString(org.w3c.dom.Document doc, String search) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			//search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(doc, XPathConstants.STRING);
			return (String) result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	static public Long getDatenAsNumber(org.w3c.dom.Document doc, String search) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			//search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(doc, XPathConstants.NUMBER);
			return Math.round((Double) result);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
}
