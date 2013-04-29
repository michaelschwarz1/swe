package de.shop.bestellverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Position;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.rest.UriHelperKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class BestellungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	/**
	 * Mit der URL /bestellungen/{id} eine Bestellung ermitteln
	 * @param id ID der Bestellung
	 * @return Objekt mit Bestelldaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Bestellung bestellung = bs.findBestellungById(id);
		if (bestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		return bestellung;
	}
	
	/**
	 * Mit der URL /bestellungen/{id}/kunde den Kunden einer Bestellung ermitteln
	 * @param id ID der Bestellung
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/kunde")
	public Kunde findKundeByBestellungId(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Kunde kunde = bs.findKundeById(id);
		if (kunde == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		return kunde;
	}

	
	/**
	 * Mit der URL /bestellungen eine neue Bestellung anlegen
	 * @param bestellung die neue Bestellung
	 * @return Objekt mit Bestelldaten, falls die ID vorhanden ist
	 */
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createBestellung(Bestellung bestellung, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final String kundeUriStr = bestellung.getKundeUri().toString();
		int startPos = kundeUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = kundeUriStr.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeIdStr, e);
		}
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN, locale);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeId);
		}
		
		final Collection<Position> positionen = bestellung.getPositionen();
		final List<Long> artikelIds = new ArrayList<>(positionen.size());
		for (Position bp : positionen) {
			final String artikelUriStr = bp.getArtikelUri().toString();
			startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException e) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			final StringBuilder sb = new StringBuilder("Keine Artikel vorhanden mit den IDs: ");
			for (Position bp : positionen) {
				final String artikelUriStr = bp.getArtikelUri().toString();
				startPos = artikelUriStr.lastIndexOf('/') + 1;
				sb.append(artikelUriStr.substring(startPos));
				sb.append(" ");
			}
			throw new NotFoundException(sb.toString());
		}

		final Collection<Artikel> gefundeneArtikel = as.findArtikelByIds(artikelIds);
		if (gefundeneArtikel.isEmpty()) {
			throw new NotFoundException("Keine Artikel vorhanden mit den IDs: " + artikelIds);
		}
		
		// Bestellpositionen haben URLs fuer persistente Artikel.
		// Diese persistenten Artikel wurden in einem DB-Zugriff ermittelt (s.o.)
		// Fuer jede Bestellposition wird der Artikel passend zur Artikel-URL bzw. Artikel-ID gesetzt.
		// Bestellpositionen mit nicht-gefundene Artikel werden eliminiert.
		int i = 0;
		final List<Position> neuepositionen = new ArrayList<>(positionen.size());
		for (Position bp : positionen) {
			// Artikel-ID der aktuellen Bestellposition (s.o.):
			// artikelIds haben gleiche Reihenfolge wie bestellpositionen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getPkArtikel().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					bp.setArtikel(artikel);
					neuepositionen.add(bp);
					break;					
				}
			}
		}
		bestellung.setPositionen(neuepositionen);
		
		// Die neue Bestellung mit den aktualisierten persistenten Artikeln abspeichern.
		// Die Bestellung darf dem Kunden noch nicht hinzugefuegt werden, weil dieser
		// sonst in einer Transaktion modifiziert werden wuerde.
		// Beim naechsten DB-Zugriff (auch lesend!) wuerde der EntityManager sonst
		// erstmal versuchen den Kunden-Datensatz in der DB zu modifizieren.
		// Dann wuerde aber der Kunde mit einer *transienten* Bestellung modifiziert werden,
		// was zwangslaeufig zu einer Inkonsistenz fuehrt!
		// Das ist die Konsequenz einer Transaktion (im Gegensatz zu den Action-Methoden von JSF!).
		bestellung = bs.createBestellung(bestellung, kunde, locale);

		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
		final Response response = Response.created(bestellungUri).build();
		LOGGER.finest(bestellungUri.toString());
		
		return response;
	}
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateBestellung(Bestellung bestellung, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandene Bestellung ermitteln
		final Bestellung origBestellung = bs.findBestellungById(bestellung.getPkBestellung());
		if (origBestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + bestellung.getPkBestellung();
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Bestellung vorher: %s", origBestellung);
	
		// Daten der vorhandenen Bestellung ueberschreiben
		origBestellung.setValues(bestellung);
		LOGGER.log(FINEST, "Bestellung nachher: %s", origBestellung);
		
		// Update durchfuehren
		bestellung = bs.updateBestellung(origBestellung);
		if (bestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + origBestellung.getPkBestellung();
			throw new NotFoundException(msg);
		}
	}

}
