package de.shop.artikelverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@PersistenceContext
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public Artikel createArtikel(Artikel artikel) {
		
		artikel.setPkArtikel(KEINE_ID);
		em.persist(artikel);
		return artikel;
	}
	
	/**
	 */
	public List<Artikel> findAlleArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_ALLE_ARTIKEL, Artikel.class)
									.getResultList();
		return result;
	}
	public List<Artikel> findArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_ALLE_ARTIKEL, Artikel.class)
									.getResultList();
		return result;
	}
	
	/**
	 */
	
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}

	/**
	 */
	public Artikel findArtikelById(Long id) {
		final Artikel artikel = em.find(Artikel.class, id);
		return artikel;
	}
	
	/**
	 */
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("pkArtikel");
		//final Path<String> idPath = a.get(Artikel_.id);   // Metamodel-Klassen funktionieren nicht mit Eclipse
		
		Predicate pred = null;
		if (ids.size() == 1) {
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}
	
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null) {
			return null;
		}

		// artikel vom EntityManager trennen, weil anschliessend z.B. nach Id und Email gesucht wird
		em.detach(artikel);

		// Wurde das Objekt konkurrierend geloescht?
		final Artikel tmp = findArtikelById(artikel.getPkArtikel());
		if (tmp == null) {
			throw new ConcurrentDeletedException(artikel.getPkArtikel());
		}
		em.detach(tmp);
		artikel = em.merge(artikel);   // OptimisticLockException
		return artikel;

	}

	
	/**
	 */
	public List<Artikel> findArtikelByBeschreibung(String beschreibung) {
		if (Strings.isNullOrEmpty(beschreibung)) {
			final List<Artikel> artikel = findVerfuegbareArtikel();
			return artikel;
		}
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BES, Artikel.class)
				                        .setParameter(Artikel.PARAM_BESCHREIBUNG, "%" + beschreibung + "%")
				                        .getResultList();
		return artikel;
	}
	
	/**
	 */
	public List<Artikel> findArtikelByMinPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_MIN_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
}
