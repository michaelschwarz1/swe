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
		private static final int NEU_AUF_LAGER = 50;
		private static final String NEUE_BESCHREIBUNG = "Hemd";
		private static final String NEUE_KATEGORIE = "Damen";
		private static final double NEUER_PREIS = 33;
		
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
		
		@Test
		public void updateArtikel() {
			LOGGER.finer("BEGINN");
			
			// Given
			final Long artikelId = ARTIKEL_ID_UPDATE;
			final String neuBeschreibung = NEUE_BESCHREIBUNG;
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
	    	
	    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuer Beschreibung bauen
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

}
