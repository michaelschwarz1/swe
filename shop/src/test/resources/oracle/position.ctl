OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE position
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	pk_position,
	fk_bestellung,
	fk_artikel,
	anzahl,
	idx,
	erzeugt,
	aktualisiert)
