CREATE SEQUENCE hibernate_sequence START WITH ${sequence.start};





CREATE TABLE kunde(
	pk_kunde INTEGER NOT NULL PRIMARY KEY,
	nachname VARCHAR2(32) NOT NULL,
	vorname VARCHAR2(32),
	email VARCHAR2(128) NOT NULL UNIQUE,
	password VARCHAR2(256),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
) CACHE;


CREATE TABLE adresse(
	pk_adresse INTEGER NOT NULL PRIMARY KEY,
	plz CHAR(5) NOT NULL,
	ort VARCHAR2(32) NOT NULL,
	strasse VARCHAR2(32),
	hausnr VARCHAR2(4),
	fk_kunde INTEGER NOT NULL REFERENCES kunde(pk_kunde),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
) CACHE;
CREATE INDEX adresse_kunde_index ON adresse(fk_kunde);


CREATE TABLE bestellung(
	pk_bestellung INTEGER NOT NULL PRIMARY KEY,
	fk_kunde INTEGER NOT NULL REFERENCES kunde(pk_kunde),
	idx SMALLINT NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
) CACHE;
CREATE INDEX bestellung_kunde_index ON bestellung(fk_kunde);

CREATE TABLE artikel(
	pk_artikel INTEGER NOT NULL PRIMARY KEY,
	kategorie VARCHAR2(32) NOT NULL,
	beschreibung VARCHAR2(32) NOT NULL,
	preis DOUBLE PRECISION,
	auf_lager SMALLINT NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
) CACHE;

CREATE TABLE position(
	pk_position INTEGER NOT NULL PRIMARY KEY,
	fk_bestellung INTEGER NOT NULL REFERENCES bestellung(pk_bestellung),
	fk_artikel INTEGER NOT NULL REFERENCES artikel(pk_artikel),
	anzahl SMALLINT NOT NULL,
	idx SMALLINT NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
) CACHE;
CREATE INDEX position_bestellung_index ON position(fk_bestellung);



