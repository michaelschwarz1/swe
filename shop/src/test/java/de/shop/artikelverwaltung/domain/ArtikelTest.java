package de.shop.artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {

	
	private static final String BESCHREIBUNG_VORHANDEN = "Schuhe";
	private static final double MIN_PREIS = 101;
	
	
	private static final String BESCHREIBUNG_NEU = "Anzug";
	private static final double PREIS_NEU = 100;	 
	private static final int AUFLAGER_NEU = 10;
	private static final String KATEGORIE_NEU = "Herren";
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findartikelByBes() {
		// Given
		final String beschreibung = BESCHREIBUNG_VORHANDEN;
		
		
		// When
		final Artikel artikel = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_BES,
                                                                        Artikel.class)
                                                      .setParameter(Artikel.PARAM_BESCHREIBUNG, beschreibung)
				                                      .getSingleResult();
		
		// Then
		assertThat(artikel.getBeschreibung(), is(beschreibung));
	}
	
	
	
	@Test
	public void findArtikelbyMinPreis() {
		// Given
		final double preis = MIN_PREIS;
		
		
		// When
		final List<Artikel> artikelList = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_MIN_PREIS,
                                                                        Artikel.class)
                                                      .setParameter(Artikel.PARAM_PREIS, preis)
				                                      .getResultList();
		
		// Then
		assertThat(artikelList.isEmpty(), is(false));
		for (Artikel a : artikelList) {
			assertThat(a.getPreis() < preis, is(true));
		}
	}
	
	
	@Test
	public void createArtikel() {
		// Given
		Artikel artikel = new Artikel();
		artikel.setBeschreibung(BESCHREIBUNG_NEU);
		artikel.setPreis(PREIS_NEU);
		artikel.setAufLager(AUFLAGER_NEU);
		artikel.setKategorie(KATEGORIE_NEU);
		
		
		// When
		try {
			getEntityManager().persist(artikel);         // abspeichern
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
		
	
         final List<Artikel> artikeln = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_BES,
                                                                     Artikel.class)
                                                   .setParameter(Artikel.PARAM_BESCHREIBUNG,
                                                  		       BESCHREIBUNG_NEU)
		                                             .getResultList();

		// Ueberpruefung des ausgelesenen Objekts
		assertThat(artikeln.size(), is(1));
		artikel = (Artikel) artikeln.get(0);
		assertThat(artikel.getPkArtikel()  > 0, is(true));
		assertThat(artikel.getBeschreibung(), is(BESCHREIBUNG_NEU));
		assertThat(artikel.getKategorie(), is(KATEGORIE_NEU));
	}

}
