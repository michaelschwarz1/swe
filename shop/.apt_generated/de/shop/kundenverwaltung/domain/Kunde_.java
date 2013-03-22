package de.shop.kundenverwaltung.domain;

import de.shop.bestellverwaltung.domain.Bestellung;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Kunde.class)
public abstract class Kunde_ {

	public static volatile SingularAttribute<Kunde, String> nachname;
	public static volatile SingularAttribute<Kunde, String> vorname;
	public static volatile SingularAttribute<Kunde, Adresse> adresse;
	public static volatile SingularAttribute<Kunde, String> email;
	public static volatile SingularAttribute<Kunde, Date> aktualisiert;
	public static volatile SingularAttribute<Kunde, Long> pkKunde;
	public static volatile SingularAttribute<Kunde, String> password;
	public static volatile SingularAttribute<Kunde, Date> erzeugt;
	public static volatile ListAttribute<Kunde, Bestellung> bestellung;

}

