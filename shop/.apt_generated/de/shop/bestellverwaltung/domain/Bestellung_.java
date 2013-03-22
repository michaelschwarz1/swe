package de.shop.bestellverwaltung.domain;

import de.shop.kundenverwaltung.domain.Kunde;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Bestellung.class)
public abstract class Bestellung_ {

	public static volatile SingularAttribute<Bestellung, Integer> idx;
	public static volatile SingularAttribute<Bestellung, Date> aktualisiert;
	public static volatile SingularAttribute<Bestellung, Long> pkBestellung;
	public static volatile SingularAttribute<Bestellung, Kunde> kunde;
	public static volatile ListAttribute<Bestellung, Position> positionen;
	public static volatile SingularAttribute<Bestellung, Date> erzeugt;

}

