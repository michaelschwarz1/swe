package de.shop.bestellverwaltung.controller;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Position;
import de.shop.util.Log;

@Named("wk")
@ConversationScoped
@Log
public class Warenkorb implements Serializable {
	private static final long serialVersionUID = -1981070683990640854L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String JSF_VIEW_WARENKORB = "/bestellverwaltung/viewWarenkorb?init=true";
	private static final int TIMEOUT = 5;
	
	private final List<Position> positionen = new ArrayList<Position>();;
	private Long artikelId;  // fuer selectArtikel.xhtml
	
	@Inject
	private transient Conversation conversation;
	
	@Inject
	private ArtikelService as;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Position> getPositionen() {
		return positionen;
	}
		
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	@Override
	public String toString() {
		return "Warenkorb " + positionen;
	}
	
	/**
	 */
	public String add(Artikel artikel) {
		beginConversation();
		
		for (Position bp : positionen) {
			if (bp.getArtikel().equals(artikel)) {
				// bereits im Warenkorb
				final int vorhandeneAnzahl = bp.getAnzahl();
				bp.setAnzahl((int) (vorhandeneAnzahl + 1));
				return JSF_VIEW_WARENKORB;
			}
		}
		
		final Position neu = new Position(artikel);
		positionen.add(neu);
		return JSF_VIEW_WARENKORB;
	}
	
	/**
	 */
	public String add() {
		final Artikel artikel = as.findArtikelById(artikelId);
		if (artikel == null) {
			return null;
		}
		
		final String outcome = add(artikel);
		artikelId = null;
		return outcome;
	}
	
	/**
	 */
	public void beginConversation() {
		if (!conversation.isTransient()) {
			return;
		}
		conversation.begin();
		conversation.setTimeout(MINUTES.toMillis(TIMEOUT));
		LOGGER.trace("Conversation beginnt");
	}
	
	/**
	 */
	public void endConversation() {
		conversation.end();
		LOGGER.trace("Conversation beendet");
	}
	
	/**
	 */
	public void remove(Position position) {
		positionen.remove(position);
		if (positionen.isEmpty()) {
			endConversation();
		}
	}
}
