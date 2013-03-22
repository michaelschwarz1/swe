package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	private static final String NACHNAME_VORHANDEN = "Schwarz";
	private static final String NACHNAME_NICHT_VORHANDEN = "Nicht";
	private static final Long ID_VORHANDEN = Long.valueOf(201);
	private static final String EMAIL_VORHANDEN = "scmi1025@hs-karlsruhe.de";
	private static final String EMAIL_NICHT_VORHANDEN = "Nicht";
	private static final String PLZ_VORHANDEN = "69168";
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Marius";
	private static final String EMAIL_NEU = "marius@test.de";
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";
	private static final String PASSWORD_NEU = "123";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findKundeByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When
		final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDR_BY_ID,
																			Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_ID, id)
				.getSingleResult();		
		// Then
		assertThat(kunde.getPkKunde(), is(id));
	}
	
	@Test
	public void findKundeByEmailVorhanden() {
		// Given
		final String email = EMAIL_VORHANDEN;
		
		// When
		final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
                                                                        Kunde.class)
                                                      .setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
				                                      .getSingleResult();
		
		// Then
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void findKundeByEmailNichtVorhanden() {
		// Given
		final String email = EMAIL_NICHT_VORHANDEN;
		
		// When
		thrown.expect(NoResultException.class);
		final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
                                            Kunde.class)
                          .setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
                          .getSingleResult();
		//Then
		assertThat(kunde.getEmail(), is(nullValue()));
	}

	@Test
	public void findKundenByNachnamenVorhanden() {
		// Given
		final String nachname = NACHNAME_VORHANDEN;
		
		// When
		final List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
                                                                               Kunde.class)
                                                            .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
				                                            .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}
	
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
                                                                               Kunde.class)
                                                            .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
				                                            .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	
	@Test
	public void findKundenByPLZVorhanden() {
		// Given
		final String plz = PLZ_VORHANDEN;
		
		// When
		final List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_PLZ,
                                                                               Kunde.class)
                                                            .setParameter(Kunde.PARAM_KUNDE_ADRESSE_PLZ, plz)
				                                            .getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getAdresse().getPlz(), is(plz));  // wie bekomm ich PLZ heraus?
		}
	}
		
	/*
	@Test
	public void createKundeOhneAdresse() throws HeuristicMixedException, HeuristicRollbackException,
	                                                  SystemException {
		// Given
		final String nachname = NACHNAME_NEU;
		final String email = EMAIL_NEU;
		
		// When
		final Privatkunde kunde = new Privatkunde();
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		getEntityManager().persist(kunde);
		
		// Then
		try {
			getUserTransaction().commit();
		}
		catch (RollbackException e) {
			final PersistenceException cause = (PersistenceException) e.getCause();
			final ConstraintViolationException cause2 = (ConstraintViolationException) cause.getCause();
			final Set<ConstraintViolation<?>> violations = cause2.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				final String msg = v.getMessage();
				if (msg.contains("Ein Kunde muss eine Adresse haben")) {
					return;
				}
			}
		}

	} */
	//@Ignore
	@Test
	public void createKunde() {
		Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setPassword(PASSWORD_NEU);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnr(HAUSNR_NEU);
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		
		try {
			getEntityManager().persist(kunde);         // abspeichern einschl. Adresse
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
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
				final List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
		                                                                               Kunde.class)
		                                                             .setParameter(Kunde.PARAM_KUNDE_NACHNAME,
		                                                            		       NACHNAME_NEU)
						                                             .getResultList();
				
				// Ueberpruefung des ausgelesenen Objekts
				assertThat(kunden.size(), is(1));
				kunde = (Kunde) kunden.get(0);
				assertThat(kunde.getPkKunde() > 0, is(true));
				assertThat(kunde.getNachname(), is(NACHNAME_NEU));
	}
}