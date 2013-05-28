package de.shop.artikelverwaltung.service;


import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class InvalidArtikelException extends AbstractArtikelValidationException {
	private static final long serialVersionUID = 4255133082483647701L;

	private Long id;
	private String beschreibung;

	public InvalidArtikelException(Artikel Artikel,
			                     Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		if (Artikel != null) {
			this.id = Artikel.getPkArtikel();
			this.beschreibung = Artikel.getBeschreibung();
		}
	}
	
	
	public InvalidArtikelException(Long id, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.id = id;
	}
	
	public InvalidArtikelException(String nachname, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.beschreibung = nachname;
	}
	
	public Long getId() {
		return id;
	}
	public String getNachname() {
		return beschreibung;
	}
	
	@Override
	public String toString() {
		return "{beschreibung=" + beschreibung + "}";
	}
}
