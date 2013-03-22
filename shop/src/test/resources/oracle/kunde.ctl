OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	pk_kunde,
	vorname,
	nachname,
	email,
	password,
	erzeugt,
	aktualisiert)
