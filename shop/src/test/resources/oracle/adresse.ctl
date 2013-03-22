OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	pk_adresse,
	plz,
	ort,
	strasse,
	hausnr,
	fk_kunde,
	erzeugt,
	aktualisiert)