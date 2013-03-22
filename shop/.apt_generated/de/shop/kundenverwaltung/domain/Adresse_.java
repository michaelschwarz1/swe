package de.shop.kundenverwaltung.domain;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Adresse.class)
public abstract class Adresse_ {

	public static volatile SingularAttribute<Adresse, String> strasse;
	public static volatile SingularAttribute<Adresse, Long> pkAdresse;
	public static volatile SingularAttribute<Adresse, Date> aktualisiert;
	public static volatile SingularAttribute<Adresse, String> plz;
	public static volatile SingularAttribute<Adresse, String> ort;
	public static volatile SingularAttribute<Adresse, Kunde> kunde;
	public static volatile SingularAttribute<Adresse, String> hausnr;
	public static volatile SingularAttribute<Adresse, Date> erzeugt;

}

