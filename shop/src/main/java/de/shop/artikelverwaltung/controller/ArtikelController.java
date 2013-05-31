package de.shop.artikelverwaltung.controller;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.InvalidArtikelException;
import de.shop.auth.controller.AuthController;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("ac")
@SessionScoped
@Log
@Stateful
@TransactionAttribute(SUPPORTS)
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	private static final String ARTIKELVERWALTUNG = "/artikelverwaltung/";
	private static final String JSF_VIEW_ARTIKEL = ARTIKELVERWALTUNG + "viewArtikel";
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	private static final String JSF_UPDATE_ARTIKEL = ARTIKELVERWALTUNG + "updateArtikel";
	
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE = "updateArtikel.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_DELETE = "updateArtikel.concurrentDelete";

	private String beschreibung;
	private String kategorie;
	private int aufLager;
	private double preis;
	private Long artikelId;
	private Artikel artikel;
	private Artikel neuerArtikel;
	private boolean geaendertArtikel;
//	private List<Artikel> ladenhueter;
	
	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;

	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private Messages messages;
	
	@Inject
	private transient HttpSession session;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerArtikelEvent;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelController [beschreibung=" + beschreibung + "]";
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

//	public List<Artikel> getLadenhueter() {
//		return ladenhueter;
//	}

	@Transactional
	public String findArtikelByBeschreibung() {
		final List<Artikel> artikel = as.findArtikelByBeschreibung(beschreibung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	
	@Transactional
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
			return JSF_SELECT_ARTIKEL;
		}
		
		final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel() {

		try {
			neuerArtikel = (Artikel) as.createArtikel(neuerArtikel);
		}
		catch (InvalidArtikelException e) {
			final String outcome = createArtikelErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getPkArtikel()));

		
		// Aufbereitung fuer viewArtikel.xhtml
		beschreibung = neuerArtikel.getBeschreibung();
		artikelId = neuerArtikel.getPkArtikel();
		artikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String createArtikelErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(InvalidArtikelException.class)) {
			final InvalidArtikelException orig = (InvalidArtikelException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}

		neuerArtikel = new Artikel();
	}	
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}
	
	@TransactionAttribute(REQUIRED)
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
				
		LOGGER.tracef("Aktualisierter Artikel: %s", artikel);
		try {
			artikel = as.updateArtikel(artikel);
		}
		catch (OptimisticLockException e) {
			final String outcome = updateErrorMsg(e, artikel.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getPkArtikel()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = artikel.getPkArtikel();
		
		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Artikel> artikelClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		 if (exceptionClass.equals(OptimisticLockException.class)) {
			if (artikelClass.equals(Artikel.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE, null);
			}
		}
		
		return null;
	}
	
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		artikel = ausgewaehlterArtikel;
		
		return Artikel.class.equals(ausgewaehlterArtikel.getClass())
			   ? JSF_UPDATE_ARTIKEL : null;
			   	}

	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}

	public void setNeuerArtikel(Artikel neuerArtikel) {
		this.neuerArtikel = neuerArtikel;
	}

	public boolean isGeaendertArtikel() {
		return geaendertArtikel;
	}

	public void setGeaendertArtikel(boolean geaendertArtikel) {
		this.geaendertArtikel = geaendertArtikel;
	}

	public double getPreis() {
		return preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public int getAufLager() {
		return aufLager;
	}

	public void setAufLager(int aufLager) {
		this.aufLager = aufLager;
	}
	

}
