package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;


	
	@RunWith(Arquillian.class)
	public class ArtikelResourceTest extends AbstractResourceTest {
		private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
		
		private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(101);
		private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(1000);
		private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(102);
//		private static final Long ARTIKEL_ID_DELETE = Long.valueOf(205);
//		private static final Long ARTIKEL_ID_DELETE_MIT_BESTELLUNGEN = Long.valueOf(203);
//		private static final Long ARTIKEL_ID_DELETE_FORBIDDEN = Long.valueOf(204);
		private static final int NEU_AUF_LAGER = 50;
		private static final String NEUE_BESCHREIBUNG = "Hemd";
		private static final String NEUE_KATEGORIE = "Damen";
		private static final double NEUER_PREIS = 33;
		
//		private static final String FILENAME = "image.gif";
//		//private static final String FILENAME = "video.mp4";
//		private static final String FILENAME_UPLOAD = "src/test/resources/rest/" + FILENAME;
//		private static final String FILENAME_DOWNLOAD = "target/" + FILENAME;
//		private static final CopyOption[] COPY_OPTIONS = { REPLACE_EXISTING };
//		private static final Long KUNDE_ID_UPLOAD = Long.valueOf(102);

//		private static final String FILENAME_INVALID_MIMETYPE = "image.bmp";	
		
		@Test
		public void validate() {
			assertThat(true, is(true));
		}
		
		@Test
		public void findArtikelById() {
			LOGGER.finer("BEGINN");
			
			// Given
			final Long artikelId = ARTIKEL_ID_VORHANDEN;
			
			// When
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
					                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
	                                         .get(ARTIKEL_ID_PATH);

			// Then
			assertThat(response.getStatusCode(), is(HTTP_OK));
			
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				final JsonObject jsonObject = jsonReader.readObject();
				assertThat(jsonObject.getJsonNumber("pkArtikel").longValue(), is(artikelId.longValue()));
			}
			
			LOGGER.finer("ENDE");
		}
		
		@Test
		public void findArtikelByIdNichtVorhanden() {
			LOGGER.finer("BEGINN");
			
			// Given
			final Long artikelId = ARTIKEL_ID_NICHT_VORHANDEN;
			
			// When
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
					                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
	                                         .get(ARTIKEL_ID_PATH);

	    	// Then
	    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
			LOGGER.finer("ENDE");
		}
	
		
//	
		
		@Test
		public void createArtikel() {
			LOGGER.finer("BEGINN");
			
			// Given
			final String username = USERNAME;
			final int aufLager = NEU_AUF_LAGER;
			final String beschreibung = NEUE_BESCHREIBUNG;
			final String kategorie = NEUE_KATEGORIE;
			final double preis = NEUER_PREIS;
			final String password = PASSWORD;

			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
			             		          .add("aufLager", aufLager)
			             		          .add("beschreibung", beschreibung)
			             		          .add("kategorie", kategorie)
			             		          .add("preis", preis)
			                              .build();

			// When
			final Response response = given().contentType(APPLICATION_JSON)
					                         .body(jsonObject.toString())
	                                         .auth()
	                                         .basic(username, password)
	                                         .post(ARTIKEL_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_CREATED));
			final String location = response.getHeader(LOCATION);
			final int startPos = location.lastIndexOf('/');
			final String idStr = location.substring(startPos + 1);
			final Long id = Long.valueOf(idStr);
			assertThat(id.longValue() > 0, is(true));

			LOGGER.finer("ENDE");
		}
		
//		
//		@Test
//		public void createKundeFalschesPassword() {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final String username = USERNAME;
//			final String password = PASSWORD_FALSCH;
//			final String nachname = NEUER_NACHNAME;
//			
//			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
//	            		                  .add("nachname", nachname)
//	            		                  .build();
//			
//			// When
//			final Response response = given().contentType(APPLICATION_JSON)
//					                         .body(jsonObject.toString())
//	                                         .auth()
//	                                         .basic(username, password)
//	                                         .post(KUNDEN_PATH);
//			
//			// Then
//			assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
//			
//			LOGGER.finer("ENDE");
//		}
//		
//		@Ignore
//		@Test
//		public void createKundeInvalid() {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final String username = USERNAME;
//			final String nachname = NEUER_NACHNAME_INVALID;
//			final String vorname = NEUER_VORNAME;
//			final String email = NEUE_EMAIL_INVALID;
//			final String password = PASSWORD;
//
//			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
//	   		                              .add("nachname", nachname)
//	   		                              .add("vorname", vorname)
//	   		                              .add("email", email)
//	   		                              .addNull("adresse")
//	   		                              .build();
//
//			// When
//			final Response response = given().contentType(APPLICATION_JSON)
//					                         .body(jsonObject.toString())
//	                                         .auth()
//	                                         .basic(username, password)
//	                                         .post(KUNDEN_PATH);
//			
//			// Then
//			assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
//			assertThat(response.asString().isEmpty(), is(false));
//			
//			LOGGER.finer("ENDE");
//		}
//		
		@Test
		public void updateArtikel() {
			LOGGER.finer("BEGINN");
			
			// Given
			final Long artikelId = ARTIKEL_ID_UPDATE;
			final int aufLager = NEU_AUF_LAGER;
			final String neuBeschreibung = NEUE_BESCHREIBUNG;
			final String kategorie = NEUE_KATEGORIE;
			final double preis = NEUER_PREIS;
			final String username = USERNAME;
			final String password = PASSWORD;
			
			// When
			Response response = given().header(ACCEPT, APPLICATION_JSON)
					                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
	                                   .get(ARTIKEL_ID_PATH);
			
			JsonObject jsonObject;
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				jsonObject = jsonReader.readObject();
			}
	    	assertThat(jsonObject.getJsonNumber("pkArtikel").longValue(), is(artikelId.longValue()));
	    	
	    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	    	final Set<String> keys = jsonObject.keySet();
	    	for (String k : keys) {
	    		if ("beschreibung".equals(k)) {
	    			job.add("beschreibung", neuBeschreibung);
	    		}
	    		else {
	    			job.add(k, jsonObject.get(k));
	    		}
	    	}
	    	jsonObject = job.build();
	    	
			response = given().contentType(APPLICATION_JSON)
					          .body(jsonObject.toString())
	                          .auth()
	                          .basic(username, password)
	                          .put(ARTIKEL_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
	   	}
//		@Ignore
//		@Test
//		public void deleteKunde() {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final Long kundeId = KUNDE_ID_DELETE;
//			final String username = USERNAME_ADMIN;
//			final String password = PASSWORD_ADMIN;
//			
//			// When
//			final Response response = given().auth()
//	                                         .basic(username, password)
//	                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//	                                         .delete(KUNDEN_ID_PATH);
//			
//			// Then
//			assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
//			LOGGER.finer("ENDE");
//		}
//		@Ignore
//		@Test
//		public void deleteKundeMitBestellung() {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final Long kundeId = KUNDE_ID_DELETE_MIT_BESTELLUNGEN;
//			final String username = USERNAME_ADMIN;
//			final String password = PASSWORD_ADMIN;
//			
//			// When
//			final Response response = given().auth()
//	                                         .basic(username, password)
//	                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//	                                         .delete(KUNDEN_ID_PATH);
//			
//			// Then
//			assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
//			final String errorMsg = response.asString();
//			assertThat(errorMsg, startsWith("Kunde mit ID=" + kundeId + " kann nicht geloescht werden:"));
//			assertThat(errorMsg, endsWith("Bestellung(en)"));
//
//			LOGGER.finer("ENDE");
//		}
//		
//		@Ignore
//		@Test
//		public void deleteKundeFehlendeBerechtigung() {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final String username = USERNAME;
//			final String password = PASSWORD;
//			final Long kundeId = KUNDE_ID_DELETE_FORBIDDEN;
//			
//			// When
//			final Response response = given().auth()
//	                                         .basic(username, password)
//	                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//	                                         .delete(KUNDEN_ID_PATH);
//			
//			// Then
//			assertThat(response.getStatusCode(), anyOf(is(HTTP_FORBIDDEN), is(HTTP_NOT_FOUND)));
//			
//			LOGGER.finer("ENDE");
//		}
//		
//		@Test
//		public void uploadDownload() throws IOException {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final Long kundeId = KUNDE_ID_UPLOAD;
//			final String fileName = FILENAME_UPLOAD;
//			final String username = USERNAME;
//			final String password = PASSWORD;
//			
//			// Datei als byte[] einlesen
//			byte[] bytes;
//			try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
//				Files.copy(Paths.get(fileName), stream);
//				bytes = stream.toByteArray();
//			}
//			
//			// byte[] als Inhalt eines JSON-Datensatzes mit Base64-Codierung
//			JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
//		                            .add("bytes", DatatypeConverter.printBase64Binary(bytes))
//		                            .build();
//			
//			// When
//			Response response = given().contentType(APPLICATION_JSON)
//					                   .body(jsonObject.toString())
//	                                   .auth()
//	                                   .basic(username, password)
//	                                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//	                                   .post(KUNDEN_ID_FILE_PATH);
//
//			// Then
//			assertThat(response.getStatusCode(), is(HTTP_CREATED));
//			// id extrahieren aus http://localhost:8080/shop2/rest/kunden/<id>/file
//			final String idStr = response.getHeader(LOCATION)
//					                     .replace(BASEURI + ":" + PORT + BASEPATH + KUNDEN_PATH + '/', "")
//					                     .replace("/file", "");
//			assertThat(idStr, is(kundeId.toString()));
//			
//			// When (2)
//			// Download der zuvor hochgeladenen Datei
//			response = given().header(ACCEPT, APPLICATION_JSON)
//					          .auth()
//	                          .basic(username, password)
//	                          .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//	                          .get(KUNDEN_ID_FILE_PATH);
//			
//			try (final JsonReader jsonReader =
//					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
//				jsonObject = jsonReader.readObject();
//			}
//			final String base64String = jsonObject.getString("bytes");
//			final byte[] downloaded = DatatypeConverter.parseBase64Binary(base64String);
//			
//			// Then (2)
//			// Dateigroesse vergleichen: hochgeladene Datei als byte[] einlesen
//			byte[] uploaded;
//			try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
//				Files.copy(Paths.get(fileName), stream);
//				uploaded = stream.toByteArray();
//			}
//			assertThat(uploaded.length, is(downloaded.length));
//			
//			// Abspeichern der heruntergeladenen Datei im Unterverzeichnis target zur manuellen Inspektion
//			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(downloaded)) {
//				Files.copy(inputStream, Paths.get(FILENAME_DOWNLOAD), COPY_OPTIONS);
//			}
//
//			LOGGER.info("Heruntergeladene Datei abgespeichert: " + FILENAME_DOWNLOAD);
//			LOGGER.finer("ENDE");
//		}
		
//		@Test
//		public void uploadInvalidMimeType() throws IOException {
//			LOGGER.finer("BEGINN");
//			
//			// Given
//			final Long kundeId = KUNDE_ID_UPLOAD;
//			final String fileName = FILENAME_UPLOAD_INVALID_MIMETYPE;
//			final String username = USERNAME;
//			final String password = PASSWORD;
//			
//			// Datei als byte[] einlesen
//			byte[] bytes;
//			try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
//				Files.copy(Paths.get(fileName), stream);
//				bytes = stream.toByteArray();
//			}
//			
//			// byte[] als Inhalt eines JSON-Datensatzes mit Base64-Codierung
//			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
//		                                  .add("bytes", DatatypeConverter.printBase64Binary(bytes))
//		                                  .build();
//			
//			// When
//			final Response response = given().contentType(APPLICATION_JSON)
//					                         .body(jsonObject.toString())
//					                         .auth()
//					                         .basic(username, password)
//					                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
//					                         .post(KUNDEN_ID_FILE_PATH);
//			
//			assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
//			assertThat(response.asString(), is(NoMimeTypeException.MESSAGE));
//		}
	}
	