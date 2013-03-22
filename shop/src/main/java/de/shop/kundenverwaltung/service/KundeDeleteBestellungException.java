package de.shop.kundenverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class KundeDeleteBestellungException extends AbstractKundeServiceException {
	private static final long serialVersionUID = 2237194289969083093L;
	private final Long kundeId;
	private final int anzahlBestellungen;
	
	public KundeDeleteBestellungException(Kunde kunde) {
		super("Kunde mit ID=" + kunde.getPkKunde() + " kann nicht geloescht werden: "
			  + kunde.getBestellung().size() + " Bestellung(en)");
		this.kundeId = kunde.getPkKunde();
		this.anzahlBestellungen = kunde.getBestellung().size();
	}

	public Long getKundeId() {
		return kundeId;
	}
	public int getAnzahlBestellungen() {
		return anzahlBestellungen;
	}
}
