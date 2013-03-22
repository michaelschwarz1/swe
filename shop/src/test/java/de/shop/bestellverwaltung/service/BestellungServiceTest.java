package de.shop.bestellverwaltung.service;


import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Position;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class BestellungServiceTest extends AbstractTest {
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(202);
	private static final Long ARTIKEL_1_ID = Long.valueOf(101);
	private static final int ARTIKEL_1_ANZAHL = 1;
	private static final Long ARTIKEL_2_ID = Long.valueOf(102);
	private static final int ARTIKEL_2_ANZAHL = 2;
	
	
//	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(101);
//	private static final String NEUE_LIEFERNR = "20100101-001";
//	private static final Long BESTELLUNG_ID2A_VORHANDEN = Long.valueOf(102);
//	private static final Long BESTELLUNG_ID2B_VORHANDEN = Long.valueOf(103);
//	private static final String NEUE_LIEFERNR2 = "20100101-002";
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	
	/**
	 */
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	/**
	 */
	@Test
	public void createBestellung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikel1Id = ARTIKEL_1_ID;
		final short artikel1Anzahl = ARTIKEL_1_ANZAHL;
		final Long artikel2Id = ARTIKEL_2_ID;
		final short artikel2Anzahl = ARTIKEL_2_ANZAHL;

		// When
		
		// An der Web-Oberflaeche wird eine Bestellung in mehrere Benutzerintaraktionen u. Transaktionen komponiert

		Artikel artikel = as.findArtikelById(artikel1Id);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Bestellung bestellung = new Bestellung();
//		List<Position> plist = new ArrayList<Position>();
//		bestellung.setPosition(plist);
		Position pos = new Position(artikel, artikel1Anzahl);
		bestellung.addPosition(pos);
		
		trans.begin();
		artikel = as.findArtikelById(artikel2Id);
		trans.commit();
		
		pos = new Position(artikel, artikel2Anzahl);
		bestellung.addPosition(pos);

		trans.begin();
		Kunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN, LOCALE);
		trans.commit();

		trans.begin();
		bestellung = bs.createBestellung(bestellung, kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(bestellung.getPositionen().size(), is(2));
		for (Position bp : bestellung.getPositionen()) {
			assertThat(bp.getArtikel().getPkArtikel(), anyOf(is(artikel1Id), is(artikel2Id)));
		}
			
		kunde = bestellung.getKunde();
		assertThat(kunde.getPkKunde(), is(kundeId));
	}
	

	/**
	 */
//	@Test
//	public void findLieferungNichtVorhanden() {
//		// Given
//		final String lieferNr = LIEFERNR_ZU_ALT;
//
//		// When
//		final List<Lieferung> lieferungen = bs.findLieferungen(lieferNr);
//		
//		// Then
//		assertThat(lieferungen.isEmpty(), is(true));
//	}
	

	
	
}
