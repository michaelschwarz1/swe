package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Kunde;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class KundeValidationException extends AbstractKundeServiceException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Kunde kunde;
	private final Collection<ConstraintViolation<Kunde>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public KundeValidationException(Kunde kunde,
			                        Collection<ConstraintViolation<Kunde>> violations) {
		super("Ungueltiger Kunde: " + kunde + ", Violations: " + violations);
		this.kunde = kunde;
		this.violations = violations;
	}
	
//	@PostConstruct
//	private void setRollbackOnly() {
//		try {
//			if (trans.getStatus() == STATUS_ACTIVE) {
//				trans.setRollbackOnly();
//			}
//		}
//		catch (IllegalStateException | SystemException e) {
//			throw new InternalError(e);
//		}
//	}

	public Kunde getKunde() {
		return kunde;
	}

	public Collection<ConstraintViolation<Kunde>> getViolations() {
		return violations;
	}
}
