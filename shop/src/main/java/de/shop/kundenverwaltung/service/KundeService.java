package de.shop.kundenverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

/**
 * Anwendungslogik fuer die KundeService
 */
@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = -5520738420154763865L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validationService;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}

	/**
	 */
	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = OrderType.ID.equals(order)
				         ? em.createNamedQuery(Kunde.FIND_KUNDEN_ORDER_BY_ID, Kunde.class)
				             .getResultList()
				         : em.createNamedQuery(Kunde.FIND_KUNDEN, Kunde.class)
				             .getResultList();
				break;

			default:
				kunden = OrderType.ID.equals(order)
		                 ? em.createNamedQuery(Kunde.FIND_KUNDEN_ORDER_BY_ID, Kunde.class)
		                	 .getResultList()
		                 : em.createNamedQuery(Kunde.FIND_KUNDEN, Kunde.class)
		                     .getResultList();
				break;
		}

		return kunden;
	}
	
	/**
	 */
	//@RolesAllowed({"sachbearbeiter", "gruppenleiter"})
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch, Locale locale) {
		validateNachname(nachname, locale);
		
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
						   .getResultList();
				break;

			default:
				kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
						   .getResultList();
				break;
		}

		return kunden;
	}
	
	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "nachname",
				                                                                           nachname,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}
	
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.FIND_NACHNAMEN_BY_PREFIX,
				                                           String.class)
				                         .setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				                         .getResultList();
		return nachnamen;
	}

	/**
	 */
	public Kunde findKundeById(Long pkKunde, FetchType fetch, Locale locale) {
		validateKundeId(pkKunde, locale);
		
		Kunde kunde = null;
		try {
			switch (fetch) {
				case NUR_KUNDE:
					kunde = em.find(Kunde.class, pkKunde);
					break;
					
				default:
					kunde = em.find(Kunde.class, pkKunde);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}

		return kunde;
	}
	
	private void validateKundeId(Long kundeId, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "pkKunde",
				                                                                           kundeId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidKundeIdException(kundeId, violations);
	}
	
	/**
	 */
	public Kunde findKundeByEmail(String email, Locale locale) {
		validateEmail(email, locale);
		try {
			final Kunde kunde = em.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
					                      .setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	private void validateEmail(String email, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "email",
				                                                                           email,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidEmailException(email, violations);
	}

	/**
	 */
	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
// 		validateKunde(kunde, locale, Default.class, PasswordGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		try {
			em.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
			  .setParameter(Kunde.PARAM_KUNDE_EMAIL, kunde.getEmail())
			  .getSingleResult();
			throw new EmailExistsException(kunde.getEmail());
		}
		catch (NoResultException e) {
			// Noch kein Kunde mit dieser Email-Adresse
			LOGGER.trace("Email-Adresse existiert noch nicht");
		}
		
		kunde.setPkKunde(KEINE_ID);
		em.persist(kunde);
		return kunde;		
	}
	
	@SuppressWarnings("unused")
	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde, groups);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	
	/**
	 */
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
//		validateKunde(kunde, locale, Default.class, PasswordGroup.class, IdGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		try {
			final Kunde vorhandenerKunde = em.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
					                                                   Kunde.class)
					                                 .setParameter(Kunde.PARAM_KUNDE_EMAIL, kunde.getEmail())
					                                 .getSingleResult();
			
			// Gibt es die Email-Adresse bei einem anderen Kunden?
			if (vorhandenerKunde.getPkKunde().longValue() != kunde.getPkKunde().longValue()) {
				throw new EmailExistsException(kunde.getEmail());
			}
		}
		catch (NoResultException e) {
			LOGGER.debugf("Neue Email-Adresse");
		}

		em.merge(kunde);
		return kunde;
	}

	/**
	 */
	@RolesAllowed("gruppenleiter")
	public void deleteKunde(Kunde kunde) {
		if (kunde == null) {
			return;
		}
		
		// Bestellungen laden, damit sie anschl. ueberprueft werden koennen
		try {
			kunde = findKundeById(kunde.getPkKunde(), FetchType.MIT_BESTELLUNGEN, Locale.getDefault());
		}
		catch (InvalidKundeIdException e) {
			return;
		}
		
		if (kunde == null) {
			return;
		}
		
		// Gibt es Bestellungen?
		if (!kunde.getBestellung().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		em.remove(kunde);
	}

	/**
	 */
	public List<Kunde> findKundenByPLZ(String plz) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_PLZ, Kunde.class)
                                             .setParameter(Kunde.PARAM_KUNDE_ADRESSE_PLZ, plz)
                                             .getResultList();
		return kunden;
	}

	/**
	 */
	public List<Kunde> findKundenBySeit(Date seit) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_DATE, Kunde.class)
                                             .setParameter(Kunde.PARAM_KUNDE_SEIT, seit)
                                             .getResultList();
		return kunden;
	}
}
