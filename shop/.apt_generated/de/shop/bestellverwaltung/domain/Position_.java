package de.shop.bestellverwaltung.domain;

import de.shop.artikelverwaltung.domain.Artikel;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Position.class)
public abstract class Position_ {

	public static volatile SingularAttribute<Position, Date> aktualisiert;
	public static volatile SingularAttribute<Position, Integer> anzahl;
	public static volatile SingularAttribute<Position, Long> pkPosition;
	public static volatile SingularAttribute<Position, Artikel> artikel;
	public static volatile SingularAttribute<Position, Date> erzeugt;
	public static volatile SingularAttribute<Position, Bestellung> bestellung;

}

