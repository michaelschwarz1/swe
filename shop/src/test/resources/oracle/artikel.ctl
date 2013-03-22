OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE artikel
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	pk_artikel,
	kategorie,
	beschreibung,
	preis,
	auf_lager,
	erzeugt,
	aktualisiert)
