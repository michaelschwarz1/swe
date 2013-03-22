package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	private static final String KUNDE_NACHNAME_VORHANDEN = "Schwarz";
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(201);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long KUNDE_ID_OHNE_BESTELLUNGEN = Long.valueOf(202);
	private static final String KUNDE_NACHNAME_NICHT_VORHANDEN = "Beta";
	private static final String KUNDE_NACHNAME_UNGUELTIG = "!§$%&/";
//	private static final String KUNDE_EMAIL_NICHT_VORHANDEN = "nicht.vorhanden@nicht.vorhanden.de";
//	private static final int TAG = 31;
//	private static final int MONAT = Calendar.JANUARY;
//	private static final int JAHR = 2001;
//	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String PLZ_VORHANDEN = "76133";
	private static final String PLZ_NICHT_VORHANDEN = "111";
	private static final String KUNDE_NEU_NACHNAME = "Alphaneu";
//	private static final String KUNDE_NEU_NACHNAME2 = "Alphanew";
	private static final String KUNDE_NEU_EMAIL = "neu.test@hska.de";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final String HAUSNR_NEU = "40";
	private static final String PASSWORD_NEU = "testpassword";	

	
	@Inject
	private KundeService ks;
	
	/**
	 */
	@Test
	public void findKundenMitNachnameVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
			
			
			// Nur zur Veranschaulichung von both().and()
			// Wenn Gleichheit mit einem anderen Namen, dann ja auch != null ...
			assertThat(k.getNachname(), both(is(notNullValue())).and(is((Object) nachname)));
			
			// Veranschaulichung von allOf: mehrere Argumente moeglich,
			// d.h. nicht nur 2 wie bei both().and()
			assertThat(k.getNachname(), allOf(is(notNullValue()), is(nachname)));
		}
	}

	/**
	 */
	@Test
	public void findKundenMitNachnameNichtVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	
	/**
	 */
	@Test
	public void findKundenMitNachnameUngueltig() {
		// Given
		final String nachname = KUNDE_NACHNAME_UNGUELTIG;

		// When / Then
		thrown.expect(InvalidNachnameException.class);
		ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, LOCALE);
	}

	/**
	 */
	@Test
	public void findKundenMitIdNichtVorhanden() {
		// Given
		final Long id = KUNDE_ID_NICHT_VORHANDEN;

		// When
		final Kunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, LOCALE);
		
		// Then
		assertThat(kunde, is(nullValue()));
	}
	
	/**
	 */
	@Test
	public void findKundenMitPLZVorhanden() {
		// Given
		final String plz = PLZ_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findKundenByPLZ(plz);

		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));
		
		for (Kunde k : kunden) {
			assertThat(k.getAdresse(), is(notNullValue()));
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}
	
	/**
	 */
	@Test
	public void findKundenMitPLZNichtVorhanden() {
		// Given
		final String plz = PLZ_NICHT_VORHANDEN;
		
		// When
		final List<Kunde> kunden = ks.findKundenByPLZ(plz);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));	}

	/**
	 */
//	@Test
//	public void findKundenByNachnameCriteria() {
//		// Given
//		final String nachname = KUNDE_NACHNAME_VORHANDEN;
//		
//		// When
//		final List<Kunde> kunden = ks.findKundenByNachnameCriteria(nachname);
//		
//		// Then
//		for (Kunde k : kunden) {
//			assertThat(k.getNachname(), is(nachname));
//		}
//	}
//	
//	/**
//	 */
//	@Test
//	public void findKundenMitMinBestMenge() {
//		// Given
//		final short minMenge = 2;
//		
//		// When
//		final Collection<Kunde> kunden = ks.findKundenMitMinBestMenge(minMenge);
//		
//		// Then
//		for (Kunde k : kunden) {
//			final Kunde kundeMitBestellungen = ks.findKundeById(k.getId(), FetchType.MIT_BESTELLUNGEN, LOCALE);
//			final List<Bestellung> bestellungen = kundeMitBestellungen.getBestellungen();
//			int bestellmenge = 0;   // short-Werte werden aufsummiert
//			for (Bestellung b : bestellungen) {
//				if (b.getBestellpositionen() == null) {
//					fail("Bestellung " + b + " ohne Bestellpositionen");
//				}
//				for (Bestellposition bp : b.getBestellpositionen()) {
//					bestellmenge += bp.getAnzahl();
//				}
//			}
//			
//			assertTrue(bestellmenge >= minMenge);
//		}
//	}

	
	/**
	 */
	@Test
	public void createKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                       SystemException, NotSupportedException {
		// Given
		final String nachname = KUNDE_NEU_NACHNAME;
		final String email = KUNDE_NEU_EMAIL;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String hausnr = HAUSNR_NEU;
		final String password = PASSWORD_NEU;
		//final String passwordWdh = password;

		// When
		final Collection<Kunde> kundenVorher = ks.findAllKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Kunde neuerKunde = new Kunde();
		neuerKunde.setNachname(nachname);
		neuerKunde.setEmail(email);
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);
		neuerKunde.setAdresse(adresse);
		adresse.setKunde(neuerKunde);
		neuerKunde.setPassword(password);
//		neuerKunde.setPasswordWdh(passwordWdh);

		final Date datumVorher = new Date();
		
		trans.begin();
//Kunde
 neuerKunde = ks.createKunde(neuerKunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(datumVorher.getTime() <= neuerKunde.getErzeugt().getTime(), is(true));

		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findAllKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		
		assertThat(kundenVorher.size() + 1, is(kundenNachher.size()));
		for (Kunde k : kundenVorher) {
			assertTrue(k.getPkKunde() < neuerKunde.getPkKunde());
			assertTrue(k.getErzeugt().getTime() < neuerKunde.getErzeugt().getTime());
		}
		
		trans.begin();
		neuerKunde = ks.findKundeById(neuerKunde.getPkKunde(), FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		
		assertThat(neuerKunde.getNachname(), is(nachname));
		assertThat(neuerKunde.getEmail(), is(email));

		
	}
	
	/**

	 */
	@Test
	public void createDuplikatKunde() throws RollbackException, HeuristicMixedException,
	                                               HeuristicRollbackException, SystemException,
	                                               NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		final Kunde k = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(k, is(notNullValue()));
		assertThat(k, is(instanceOf(Kunde.class)));
		
		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setNachname(k.getNachname());
		neuerKunde.setVorname(k.getVorname());
		neuerKunde.setEmail(k.getEmail());
		neuerKunde.setAdresse(k.getAdresse());
		
		// Then
		thrown.expect(EmailExistsException.class);
		trans.begin();
		ks.createKunde(neuerKunde, LOCALE);
	}
	
	/**
	 */
	//TODO
//	@Test
//	public void createKundeOhneAdresse() {
//		// Given
//		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;
//		final String email = KUNDE_EMAIL_NICHT_VORHANDEN;
//		
//		// When
//		final Kunde neuerKunde = new Kunde();
//		neuerKunde.setNachname(nachname);
//		neuerKunde.setEmail(email);
//		neuerKunde.setAdresse(null);
//		
//		// Then
//		thrown.expect(KundeValidationException.class);
//		thrown.expectMessage("Ungueltiger Kunde:");
//		ks.createKunde(neuerKunde, LOCALE);
//	}
	
//	/**
//	 */
//	@Test
//	public void createKundeFalschesPassword() {
//		// Given
//		final String nachname = KUNDE_NEU_NACHNAME2;
//		final String email = KUNDE_NEU_EMAIL2;
//		final String plz = PLZ_NEU;
//		final String ort = ORT_NEU;
//		final String strasse = STRASSE_NEU;
//		final String hausnr = HAUSNR_NEU;
//		final String password = PASSWORD_NEU;
////		final String passwordWdh = PASSWORD_FALSCH_NEU;
//
//		// When
//		Kunde neuerKunde = new Kunde();
//		neuerKunde.setNachname(nachname);
//		neuerKunde.setEmail(email);
//		final Adresse adresse = new Adresse();
//		adresse.setPlz(plz);
//		adresse.setOrt(ort);
//		adresse.setStrasse(strasse);
//		adresse.setHausnr(hausnr);
//		neuerKunde.setAdresse(adresse);
//		neuerKunde.setPassword(password);
////		neuerKunde.setPasswordWdh(passwordWdh);
//
//		// Then
//		thrown.expect(KundeValidationException.class);
//		thrown.expectMessage("kundenverwaltung.kunde.password.notEqual");
//		ks.createKunde(neuerKunde, LOCALE);
//	}
	
	/**
	 */
	@Test
	public void deleteKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_OHNE_BESTELLUNGEN;

		final Collection<Kunde> kundenVorher = ks.findAllKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
	
		// When
		trans.begin();
		final Kunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN, LOCALE);
		trans.commit();
		trans.begin();
		ks.deleteKunde(kunde);
		trans.commit();
	
		// Then
		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findAllKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		assertThat(kundenVorher.size() - 1, is(kundenNachher.size()));
	}
	
	/**
	 */
	@Test
	public void neuerNameFuerKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String alterNachname = kunde.getNachname();
		final String neuerNachname =  alterNachname + alterNachname.charAt(alterNachname.length() - 1); //"Neumann";
		kunde.setNachname(neuerNachname);
	
		trans.begin();
		kunde = ks.updateKunde(kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(kunde.getNachname(), is(neuerNachname));
		trans.begin();
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		assertThat(kunde.getNachname(), is(neuerNachname));
	}
	


}
