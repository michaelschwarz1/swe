package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;

public interface BestellungService {

	/**
	 */
	Bestellung findBestellungById(Long id);

	/**
	 */

	Kunde findKundeById(Long id);

	/**
	 */
	List<Bestellung> findBestellungenByKundeId(Long kundeId);

	/**
	 */
	
	Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale);
	
	/**
	 */
	
	Bestellung updateBestellung(Bestellung bestellung);

	/**
	 */
	List<Artikel> ladenhueter(int anzahl);
}
