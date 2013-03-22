package de.shop.artikelverwaltung.domain;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Artikel.class)
public abstract class Artikel_ {

	public static volatile SingularAttribute<Artikel, Long> pkArtikel;
	public static volatile SingularAttribute<Artikel, Date> aktualisiert;
	public static volatile SingularAttribute<Artikel, Double> preis;
	public static volatile SingularAttribute<Artikel, String> beschreibung;
	public static volatile SingularAttribute<Artikel, Integer> aufLager;
	public static volatile SingularAttribute<Artikel, String> kategorie;
	public static volatile SingularAttribute<Artikel, Date> erzeugt;

}

