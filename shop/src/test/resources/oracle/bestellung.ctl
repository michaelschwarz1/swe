OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"'  (
	pk_bestellung,
	fk_kunde,
	idx,
	erzeugt,
	aktualisiert)
