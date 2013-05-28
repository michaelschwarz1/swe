package de.shop.kundenverwaltung.rest;

import static java.util.logging.Level.FINEST;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.rest.BestellungResource;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;


@ApplicationScoped
@Log
public class UriHelperKunde {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KundeResource.class)
		                             .path(KundeResource.class, "findKundeById");
		final URI kundeUri = ub.build(kunde.getPkKunde());
		return kundeUri;
	}
	
	
	public void updateUriKunde(Kunde kunde, UriInfo uriInfo) {
		// URL fuer Bestellungen setzen
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
                                     .path(BestellungResource.class)
                                     .path(BestellungResource.class, "findBestellungById");
		final URI bestellungenUri = ub.build(kunde.getPkKunde());
		kunde.setBestellungenUri(bestellungenUri);
		
		final URI fileUri = getUriDownload(kunde.getPkKunde(), uriInfo);
		kunde.setFileUri(fileUri);
		
		LOGGER.log(FINEST, "Kunde: {0}", kunde);
	}
	public URI getUriDownload(Long kundeId, UriInfo uriInfo) {
		final URI uri = uriInfo.getBaseUriBuilder()
		                       .path(KundeResource.class)
		                       .path(KundeResource.class, "download")
		                       .build(kundeId);
		return uri;
	}
}
