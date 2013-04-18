DROP TABLE rolle;
CREATE TABLE rolle(id NUMBER(1) NOT NULL PRIMARY KEY, name VARCHAR2(32) NOT NULL) CACHE;
INSERT INTO rolle VALUES (0, 'admin');
INSERT INTO rolle VALUES (1, 'mitarbeiter');
INSERT INTO rolle VALUES (2, 'abteilungsleiter');
INSERT INTO rolle VALUES (3, 'kunde');


--=====================
-- Constraint
--=====================
ALTER TABLE kunde_rolle ADD CONSTRAINT kunde_rolle__rolle_fk FOREIGN KEY (fk_rolle) REFERENCES rolle;