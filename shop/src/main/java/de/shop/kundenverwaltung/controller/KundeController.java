package de.shop.kundenverwaltung.controller;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;
import org.richfaces.component.SortOrder;
import org.richfaces.component.UIPanelMenuItem;

import de.shop.auth.controller.AuthController;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.InvalidKundeException;
import de.shop.kundenverwaltung.service.InvalidNachnameException;
import de.shop.kundenverwaltung.service.KundeDeleteBestellungException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.kundenverwaltung.service.KundeService.OrderType;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Messages;

/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
//	private static final int MAX_AUTOCOMPLETE = 10;

	private static final String JSF_KUNDENVERWALTUNG = "/kundenverwaltung/";
	private static final String JSF_VIEW_KUNDE = JSF_KUNDENVERWALTUNG + "viewKunde";
	private static final String JSF_LIST_KUNDEN = JSF_KUNDENVERWALTUNG + "/kundenverwaltung/listKunden";
	private static final String JSF_UPDATE_KUNDE = JSF_KUNDENVERWALTUNG + "updateKunde";
	private static final String JSF_DELETE_OK = JSF_KUNDENVERWALTUNG + "okDelete";
	
	private static final String REQUEST_KUNDE_ID = "kundeId";

	private static final String CLIENT_ID_KUNDEID = "form:kundeIdInput";
	private static final String MSG_KEY_KUNDE_NOT_FOUND_BY_ID = "viewKunde.notFound";
	
	private static final String CLIENT_ID_KUNDEN_NACHNAME = "form:nachname";
//	private static final String MSG_KEY_KUNDEN_NOT_FOUND_BY_NACHNAME = "listKunden.notFound";

	private static final String CLIENT_ID_CREATE_EMAIL = "createKundeForm:email";
	private static final String MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS = "createKunde.emailExists";
	
	private static final Class<?>[] PASSWORD_GROUP = { PasswordGroup.class };
	
	private static final String CLIENT_ID_UPDATE_PASSWORD = "updateKundeForm:password";
	private static final String CLIENT_ID_UPDATE_EMAIL = "updateKundeForm:email";
	private static final String MSG_KEY_UPDATE_KUNDE_DUPLIKAT = "updateKunde.duplikat";
	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE = "updateKunde.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE = "updateKunde.concurrentDelete";
	
	//private static final String CLIENT_ID_SELECT_DELETE_BUTTON_PREFIX = "form:kundenTabelle:";
	//private static final String CLIENT_ID_SELECT_DELETE_BUTTON_SUFFIX = ":deleteButton";
	private static final String MSG_KEY_SELECT_DELETE_KUNDE_BESTELLUNG = "listKunden.deleteKundeBestellung";
	
	private static final String CLIENT_ID_DELETE_BUTTON = "form:deleteButton";
	private static final String MSG_KEY_DELETE_KUNDE_BESTELLUNG = "viewKunde.deleteKundeBestellung";
	
	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private transient HttpServletRequest request;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Messages messages;

	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerKundeEvent;
	
	@Inject
	@Push(topic = "updateKunde")
	private transient Event<String> updateKundeEvent;
	
//	@Inject
//	private FileHelper fileHelper;

	private Long kundeId;
	private Kunde kunde;
	
	private String nachname;
	
	private List<Kunde> kunden = Collections.emptyList();
	
	private SortOrder vornameSortOrder = SortOrder.unsorted;
	private String vornameFilter = "";
	
	private boolean geaendertKunde;    // fuer ValueChangeListener
	private Kunde neuerKunde;
	
//	private byte[] bytes;
//	private String contentType;

	private transient UIPanelMenuItem menuItemEmail;   // eigentlich nicht dynamisch, nur zur Demo
	
	@PostConstruct
	private void postConstruct() {
//		// Dynamischer Menuepunkt fuer Emails
//		final Application app = facesCtx.getApplication();  // javax.faces.application.Application
//		menuItemEmail = (UIPanelMenuItem) app.createComponent(facesCtx,
//				                                              UIPanelMenuItem.COMPONENT_TYPE,
//				                                              PanelMenuItemRenderer.class.getName());
//		menuItemEmail.setLabel("Email dynamisch");
//		menuItemEmail.setId("kundenverwaltungViewByEmail");
//		
//		// <h:outputLink>
//		// component-family: javax.faces.Output renderer-type: javax.faces.Link
//		
//		//menuGroup = (UIPanelMenuGroup) app.createComponent(facesCtx,
//		//                                                   UIPanelMenuGroup.COMPONENT_TYPE,
//		//                                                   PanelMenuGroupRenderer.class.getName());
//		//UIPanelMenuItem item = (UIPanelMenuItem) app.createComponent(facesCtx,
//		//                                                             UIPanelMenuItem.COMPONENT_TYPE,
//		//                                                             PanelMenuItemRenderer.class.getName());
//		//menuGroup.getChildren().add(item);

		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@Override
	public String toString() {
		return "KundenverwaltungController [kundeId=" + kundeId + ", nachname=" + nachname
		       + ", geaendertKunde=" + geaendertKunde + "]";
	}
	
	public Long getKundeId() {
		return kundeId;
	}

	public void setKundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public List<Kunde> getKunden() {
		return kunden;
	}

	public SortOrder getVornameSortOrder() {
		return vornameSortOrder;
	}

	public void setVornameSortOrder(SortOrder vornameSortOrder) {
		this.vornameSortOrder = vornameSortOrder;
	}

	public void sortByVorname() {
		vornameSortOrder = vornameSortOrder.equals(SortOrder.ascending)
						   ? SortOrder.descending
						   : SortOrder.ascending;
	} 
	
	public String getVornameFilter() {
		return vornameFilter;
	}
	
	public void setVornameFilter(String vornameFilter) {
		this.vornameFilter = vornameFilter;
	}

	
	public void setMenuItemEmail(UIPanelMenuItem menuItemEmail) {
		this.menuItemEmail = menuItemEmail;
	}
	public UIPanelMenuItem getMenuItemEmail() {
		return menuItemEmail;
	}

	public Date getAktuellesDatum() {
		final Date datum = new Date();
		return datum;
	}
	
	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundeById() {
		// Bestellungen werden durch "Extended Persistence Context" nachgeladen
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, locale);
		
		if (kunde == null) {
			// Kein Kunde zu gegebener ID gefunden
			return findKundeByIdErrorMsg(kundeId.toString());
		}

		return JSF_VIEW_KUNDE;
	}
	

	private String findKundeByIdErrorMsg(String id) {
		messages.error(KUNDENVERWALTUNG, MSG_KEY_KUNDE_NOT_FOUND_BY_ID, CLIENT_ID_KUNDEID, id);
		return null;
	}
	
	/**
	 * F&uuml;r rich:autocomplete
	 * @return Liste der potenziellen Kunden
	 */
//	@TransactionAttribute(REQUIRED)
//	public List<Kunde> findKundenByIdPrefix(String idPrefix) {
//		List<Kunde> kundenPrefix = null;
//		Long id = null; 
//		try {
//			id = Long.valueOf(idPrefix);
//		}
//		catch (NumberFormatException e) {
//			findKundeByIdErrorMsg(idPrefix);
//			return null;
//		}
//		
//		kundenPrefix = ks.findKundenByIdPrefix(id);
//		if (kundenPrefix == null || kundenPrefix.isEmpty()) {
//			// Kein Kunde zu gegebenem ID-Praefix vorhanden
//			findKundeByIdErrorMsg(idPrefix);
//			return null;
//		}
//		
//		if (kundenPrefix.size() > MAX_AUTOCOMPLETE) {
//			return kundenPrefix.subList(0, MAX_AUTOCOMPLETE);
//		}
//		return kundenPrefix;
//	}
//	
	@TransactionAttribute(REQUIRED)
	public void loadKundeById() {
		// Request-Parameter "kundeId" fuer ID des gesuchten Kunden
		final String idStr = request.getParameter("PK_Kunde");
		Long id;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		// Suche durch den Anwendungskern
		kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			return;
		}
	}
	
	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundenByNachname() {
		if (nachname == null || nachname.isEmpty()) {
			kunden = ks.findAllKunden(FetchType.MIT_BESTELLUNGEN, OrderType.KEINE);
			return JSF_LIST_KUNDEN;
		}

		try {
			kunden = ks.findKundenByNachname(nachname, FetchType.MIT_BESTELLUNGEN, locale);
		}
		catch (InvalidNachnameException e) {
			final Collection<ConstraintViolation<Kunde>> violations = e.getViolations();
			messages.error(violations, CLIENT_ID_KUNDEN_NACHNAME);
			return null;
		}
		return JSF_LIST_KUNDEN;
	}
	
	/**
	 * F&uuml;r rich:autocomplete
	 * @return Liste der potenziellen Nachnamen
	 */
//	@TransactionAttribute(REQUIRED)
//	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
//		// NICHT: Liste von Kunden. Sonst waeren gleiche Nachnamen mehrfach vorhanden.
//		final List<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
//		if (nachnamen.isEmpty()) {
//			messages.error(KUNDENVERWALTUNG, MSG_KEY_KUNDEN_NOT_FOUND_BY_NACHNAME, CLIENT_ID_KUNDEN_NACHNAME, kundeId);
//			return nachnamen;
//		}
//
//		if (nachnamen.size() > MAX_AUTOCOMPLETE) {
//			return nachnamen.subList(0, MAX_AUTOCOMPLETE);
//		}
//
//		return nachnamen;
//	}
	
	@TransactionAttribute(REQUIRED)
	public String details(Kunde ausgewaehlterKunde) {
		if (ausgewaehlterKunde == null) {
			return null;
		}
		
		// Bestellungen nachladen
		this.kunde = ks.findKundeById(ausgewaehlterKunde.getPkKunde(), FetchType.MIT_BESTELLUNGEN, locale);
		this.kundeId = this.kunde.getPkKunde();
		
		return JSF_VIEW_KUNDE;
	}
	
	@TransactionAttribute(REQUIRED)
	public String createKunde() {
		// Liste von Strings als Set von Enums konvertieren
//		final Set<HobbyType> hobbiesPrivatkunde = new HashSet<>();
//		for (String s : hobbies) {
//			hobbiesPrivatkunde.add(HobbyType.valueOf(s));
//		}
//		neuerPrivatkunde.setHobbies(hobbiesPrivatkunde);

		try {
			neuerKunde = (Kunde) ks.createKunde(neuerKunde, locale);
		}
		catch (InvalidKundeException | EmailExistsException e) {
			final String outcome = createKundeErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerKunde.getPkKunde()));
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerKunde.getPkKunde();
		kunde = neuerKunde;
		neuerKunde = null;  // zuruecksetzen
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}

	private String createKundeErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(EmailExistsException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS, CLIENT_ID_CREATE_EMAIL);
		}
		else if (exceptionClass.equals(InvalidKundeException.class)) {
			final InvalidKundeException orig = (InvalidKundeException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}

	public void createEmptyKunde() {
		if (neuerKunde != null) {
			return;
		}

		neuerKunde = new Kunde();
		final Adresse adresse = new Adresse();
		adresse.setKunde(neuerKunde);
		neuerKunde.setAdresse(adresse);
		
//		final int anzahlHobbies = HobbyType.values().length;
//		hobbies = new ArrayList<>(anzahlHobbies);
	}
	
	/**
	 * https://issues.jboss.org/browse/AS7-1348
	 * http://community.jboss.org/thread/169487
	 */
	public Class<?>[] getPasswordGroup() {
		return PASSWORD_GROUP.clone();
	}

	/**
	 * Hobbies als Liste von Strings fuer JSF aufbereiten, wenn ein existierender Privatkunde
	 * in updatePrivatkunde.xhtml aktualisiert wird
	 */
//	public void copyHobbies() {
//		hobbies = new ArrayList<>(HobbyType.values().length);
//
//		final Privatkunde privatkunde = (Privatkunde) kunde;
//		final Set<HobbyType> hobbiesPrivatkunde = privatkunde.getHobbies();
//		for (HobbyType h : hobbiesPrivatkunde) {
//			hobbies.add(h.name());
//		}
//	}

	/**
	 * Verwendung als ValueChangeListener bei updatePrivatkunde.xhtml und updateFirmenkunde.xhtml
	 */
	public void geaendert(ValueChangeEvent e) {
		if (geaendertKunde) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertKunde = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertKunde = true;				
		}
	}
	

	@TransactionAttribute(REQUIRED)
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertKunde || kunde == null) {
			return JSF_INDEX;
		}
		
//		if (kunde.getClass().equals(Kunde.class)) {
//			final Kunde kunde = (Kunde) kunde;
////			final Set<HobbyType> hobbiesPrivatkunde = privatkunde.getHobbies();
////			hobbiesPrivatkunde.clear();
////			
////			for (String s : hobbies) {
////				hobbiesPrivatkunde.add(HobbyType.valueOf(s));				
////			}
//		}
		
		LOGGER.tracef("Aktualisierter Kunde: %s", kunde);
		try {
			kunde = ks.updateKunde(kunde, locale);
		}
		catch (EmailExistsException | InvalidKundeException
			  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateErrorMsg(e, kunde.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateKundeEvent.fire(String.valueOf(kunde.getPkKunde()));
		
		// ValueChangeListener zuruecksetzen
		geaendertKunde = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = kunde.getPkKunde();
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Kunde> kundeClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (exceptionClass.equals(InvalidKundeException.class)) {
			// Ungueltiges Password: Attribute wurden bereits von JSF validiert
			final InvalidKundeException orig = (InvalidKundeException) e;
			final Collection<ConstraintViolation<Kunde>> violations = orig.getViolations();
			messages.error(violations, CLIENT_ID_UPDATE_PASSWORD);
		}
		else if (exceptionClass.equals(EmailExistsException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
			}
//			else {
//				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_FIRMENKUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
//			}
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE, null);
			}
		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE, null);
			}
//			else {
//				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_FIRMENKUNDE_CONCURRENT_DELETE, null);
//			}
		}
		return null;
	}
	
	/**
	 * Action Methode, um einen zuvor gesuchten Kunden zu l&ouml;schen
	 * @return URL fuer Startseite im Erfolgsfall, sonst wieder die gleiche Seite
	 */
	@TransactionAttribute(REQUIRED)
	public String deleteAngezeigtenKunden() {
		if (kunde == null) {
			return null;
		}
		
		LOGGER.trace(kunde);
		try {
			ks.deleteKunde(kunde);
		}
		catch (KundeDeleteBestellungException e) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_DELETE_KUNDE_BESTELLUNG, CLIENT_ID_DELETE_BUTTON,
					       e.getKundeId(), e.getAnzahlBestellungen());
			return null;
		}
		
		// Aufbereitung fuer ok.xhtml
		request.setAttribute(REQUEST_KUNDE_ID, kunde.getPkKunde());
		
		// Zuruecksetzen
		kunde = null;
		kundeId = null;

		return JSF_DELETE_OK;
	}
	
	public String selectForUpdate(Kunde ausgewaehlterKunde) {
		if (ausgewaehlterKunde == null) {
			return null;
		}
		
		kunde = ausgewaehlterKunde;
		
		return Kunde.class.equals(ausgewaehlterKunde.getClass())
			   ? JSF_UPDATE_KUNDE : null;
			   	}

	@TransactionAttribute(REQUIRED)
	public String delete(Kunde ausgewaehlterKunde) {
		try {
			ks.deleteKunde(ausgewaehlterKunde);
		}
		catch (KundeDeleteBestellungException e) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_SELECT_DELETE_KUNDE_BESTELLUNG, null,
					       e.getKundeId(), e.getAnzahlBestellungen());
			return null;
		}

		kunden.remove(ausgewaehlterKunde);
		return null;
	}

//	public void uploadListener(FileUploadEvent event) {
//		final UploadedFile uploadedFile = event.getUploadedFile();
//		contentType = uploadedFile.getContentType();
//		bytes = uploadedFile.getData();
//	}

//	@TransactionAttribute(REQUIRED)
//	public String upload() {
//		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, locale);
//		if (kunde == null) {
//			return null;
//		}
//		ks.setFile(kunde, bytes, contentType);
//
//		kundeId = null;
//		bytes = null;
//		contentType = null;
//		kunde = null;
//
//		return JSF_INDEX;
//	}
//	
//	public String getFilename(File file) {
//		if (file == null) {
//			return "";
//		}
//		
//		fileHelper.store(file);
//		return file.getFilename();
//	}
//	
//	public String getBase64(File file) {
//		return DatatypeConverter.printBase64Binary(file.getBytes());
//	}
}
