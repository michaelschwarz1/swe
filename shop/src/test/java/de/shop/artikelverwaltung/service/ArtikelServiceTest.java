package de.shop.artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

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

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {

	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(104);
	private static final String BESCHREIBUNG_VORHANDEN = "Schuhe";


	private static final String BESCHREIBUNG_NEU = "Anzug";
	private static final double PREIS_NEU = 100;	 
	private static final int AUFLAGER_NEU = 10;
	private static final String KATEGORIE_NEU = "Herren";
	
	@Inject 
	private ArtikelService as;
	
	/**
	 */
	
	@Test
	public void findArtikelMitIdVorhanden() {
		//Given
		final Long id = ARTIKEL_ID_VORHANDEN;
		//When
		final Artikel artikel = as.findArtikelById(id);
		//Then
		assertThat(artikel, is(notNullValue()));
		
	}
	
	/**
	 */
	
	@Test
	public void findArtikelMitBeschreibungVorhanden() {
		//Given
		final String beschreibung = BESCHREIBUNG_VORHANDEN;
		//When
		final List<Artikel> artikel = as.findArtikelByBeschreibung(beschreibung);
		//Then
		assertThat(artikel, is(notNullValue()));
		assertThat(artikel.isEmpty(), is(false));
		
		for (Artikel a : artikel) {
			assertThat(a.getBeschreibung(), is(beschreibung));
		}
	}
	
	/**
	 */
	
	@Test
	public void createArtikel() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException {
		
		//Given
		final String beschreibung = BESCHREIBUNG_NEU;
		final double preis = PREIS_NEU;	 
		final int aufLager = AUFLAGER_NEU;
		final String kategorie = KATEGORIE_NEU;
		
		//When
		final Collection<Artikel> artikelVorher = as.findAlleArtikel();
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Artikel neuerArtikel = new Artikel();
		neuerArtikel.setBeschreibung(beschreibung);
		neuerArtikel.setAufLager(aufLager);
		neuerArtikel.setKategorie(kategorie);
		neuerArtikel.setPreis(preis);
		
		final Date datumVorher = new Date();
		
		trans.begin();
		
		neuerArtikel = as.createArtikel(neuerArtikel);
		trans.commit();
		
		//Then
		
		assertThat(datumVorher.getTime() <= neuerArtikel.getErzeugt().getTime(), is(true));

		trans.begin();
		final Collection<Artikel> artikelNachher = as.findAlleArtikel();
		trans.commit();
		
		assertThat(artikelVorher.size() + 1, is(artikelNachher.size()));
		for (Artikel a : artikelVorher) {
			assertTrue(a.getPkArtikel() < neuerArtikel.getPkArtikel());
			assertTrue(a.getErzeugt().getTime() < neuerArtikel.getErzeugt().getTime());
		}
		
		trans.begin();
		neuerArtikel = as.findArtikelById(neuerArtikel.getPkArtikel());
		trans.commit();
		
		assertThat(neuerArtikel.getBeschreibung(), is(beschreibung));
		assertThat(neuerArtikel.getPreis(), is(preis));
		
	}
	
}
