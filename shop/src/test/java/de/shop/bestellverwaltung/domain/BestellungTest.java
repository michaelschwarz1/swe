
package de.shop.bestellverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;



@RunWith(Arquillian.class)

public class BestellungTest extends AbstractDomainTest {
	
	private static final Long IDVORHANDEN = Long.valueOf(205);
	private static final String EMAIL_VORHANDEN = "sebastian@hs-karlsruhe.de"; 
	private static final String NACHNAME_VORHANDEN ="Gart";
//	private static final Long ARTIKEL_VORHANDEN = Long.valueOf(104);
//	
//	private static final String NACHNAME_NEU = "Uhland";
//	private static final String VORNAME_NEU = "Ute";
//	private static final String NEU_EMAIL = "ute@test.de";
//	private static final int NEU_BESTELLPOS_ANZAHL_ARTIKEL = 1;
//	
//	private static final String PLZ_NEU = "31123";
//	private static final String ORT_NEU = "Freiburg";
//	private static final String STRASSE_NEU = "utestrasse";
//	private static final String HAUSNR_NEU = "16";
	
	@PersistenceContext
	private EntityManager em;
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findKundeByEmail() {
	
		final String email = EMAIL_VORHANDEN;
		Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
			.setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
			.getSingleResult();
	assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void findBestellungenByNachname() {
		// Given
		final String nn = NACHNAME_VORHANDEN;
		
		
		// When
		final List<Bestellung> bestList  = getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_NACHNAME,
                                                                        Bestellung.class)
                                                      .setParameter(Bestellung.PARAM_KUNDE_NACHNAME, nn)
				                                      .getResultList();
		
		// Then
		assertThat(bestList.isEmpty(), is(false));
		//assertThat(bestList., is(kunde));
		for (Bestellung a : bestList) {
			assertThat(a.getKunde().getNachname(), is(nn));
		}
	}
	
	
	@Test
	public void findBestellungenByKunde() {
		// Given
		final Long kundeId = IDVORHANDEN;
		Kunde kunde = getEntityManager().find(Kunde.class, kundeId);
		assertThat(kunde.getNachname() , is("Schmitt"));
		// When
		final List<Bestellung> bestList  = getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                                                                        Bestellung.class)
                                                      .setParameter(Bestellung.PARAM_KUNDEID, kundeId)
				                                      .getResultList();
		
		// Then
		assertThat(bestList.isEmpty(), is(false));
		//assertThat(bestList., is(kunde));
		for (Bestellung a : bestList) {
			assertThat(a.getKunde().getPkKunde(), is(kundeId));
		}
	}
		

	
	@SuppressWarnings("static-access")
	@Test
	public void createBestellung() {
		final Long kundeId = IDVORHANDEN;
		Kunde kunde = getEntityManager().find(Kunde.class, kundeId);
		assertThat(kunde.getNachname() , is("Schmitt"));
		System.out.println("Kunde gefunden mit Name" + kunde.getNachname());
		// Given
					
//		Kunde kunde = new Kunde();
//		kunde.setVorname(NACHNAME_NEU);
//		kunde.setNachname(VORNAME_NEU);
//		kunde.setEmail(NEU_EMAIL);
//		kunde.setGeschlecht(GESCHLECHT_NEU);
//	
//		
//		Adresse adresse = new Adresse();
//		adresse.setPlz(PLZ_NEU);
//		adresse.setOrt(ORT_NEU);
//		adresse.setStrasse(STRASSE_NEU);
//		adresse.setHausnr(HAUSNR_NEU);
//		adresse.addKunde(kunde);
//		
//		kunde.setAdresse(adresse);
//		
//		try {
//			getEntityManager().persist();         // abspeichern
//		}
//		catch (ConstraintViolationException e) {
//			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
//			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//			for (ConstraintViolation<?> v : violations) {
//				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
//				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
//				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
//			}
//			
//			throw new RuntimeException(e);
//		}
		
		
		Bestellung bestellung = new Bestellung();
		bestellung.setKunde(kunde);
		
					
		// When
		try {
			getEntityManager().persist(bestellung);         // abspeichern
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
// Then
		
         final List<Bestellung> bestellungn = getEntityManager().createNamedQuery(bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                                                                     Bestellung.class)
                                                   .setParameter(bestellung.PARAM_KUNDEID,
                                                		   IDVORHANDEN)
		                                             .getResultList();

		 //Ueberpruefung des ausgelesenen Objekts
		assertThat(bestellungn.size(), is(2));
		bestellung = (Bestellung) bestellungn.get(0);
		assertThat(bestellung.getPkBestellung()  > 0, is(true));
		assertThat(bestellung.getKunde().getPkKunde(), is(IDVORHANDEN));

	
}

}
