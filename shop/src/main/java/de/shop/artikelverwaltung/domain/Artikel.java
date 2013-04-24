package de.shop.artikelverwaltung.domain;


import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sun.istack.NotNull;


/**
 * The persistent class for the ARTIKEL database table.
 * 
 */
@Entity

@Table(name = "artikel")
@NamedQueries({
@NamedQuery(name = Artikel.FIND_ALLE_ARTIKEL,
				query = "SELECT a" 
				+ " FROM Artikel a"
				), 
@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_ID,
            	query = "SELECT a"
            	+ " FROM    Artikel a"
            	+ " WHERE    a.id LIKE :" + Artikel.PARAM_ID
            	), 
@NamedQuery(name = Artikel.FIND_VERFUEGBARE_ARTIKEL,
            	query = "SELECT a"
            	+ " FROM    Artikel a"
            	+ " WHERE    a.aufLager > 0"
            	+ " ORDER BY a.aufLager ASC"
            	),
@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_BES,
            	query = "SELECT a"
            			+ " FROM Artikel a"
            			+ " WHERE a.beschreibung LIKE :" + Artikel.PARAM_BESCHREIBUNG
						+ " AND a.aufLager >0"
						+ " ORDER BY a.pkArtikel ASC"
				),
@NamedQuery(name = Artikel.FIND_ARTIKEL_MIN_PREIS,
            	query = "SELECT a"
            			+ " FROM Artikel a"
						+ " WHERE a.preis < :" + Artikel.PARAM_PREIS
			 	        + " ORDER BY a.id DESC")
})

public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PREFIX = "Artikel.";
	public static final String FIND_ALLE_ARTIKEL = PREFIX + "findAlleArtikel";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_BES = PREFIX + "findArtikelByBes";
	public static final String FIND_ARTIKEL_MIN_PREIS = PREFIX + "findArtikelByMinPreis";
	public static final String FIND_ARTIKEL_BY_ID = PREFIX + "findArtikelById";
	public static final String PARAM_BESCHREIBUNG = "beschreibung";
	public static final String PARAM_PREIS = "preis";	
	public static final String PARAM_ID = "pkArtikel";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_ARTIKEL", unique = true, nullable = false, updatable = false)
	@JsonProperty
	private Long pkArtikel;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(name = "AUF_LAGER")
	@Min(0)
	@JsonProperty
	private int aufLager;
	
    @Column(name = "BESCHREIBUNG")
    @NotNull
    @Size(min = 2, max = 32)
    @Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
    @JsonProperty
    private String beschreibung;
    
    @Column(name = "KATEGORIE")
    @NotNull
    @Size(min = 2, max = 32)
    @Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
    @JsonProperty
	private String kategorie;

	@Column(name = "PREIS")
	@NotNull
	@JsonProperty
	private double preis;

	public Artikel() {
		super();
	}

	@JsonIgnore
	public Long getPkArtikel() {
		return this.pkArtikel;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public void setPkArtikel(Long pkArtikel) {
		this.pkArtikel = pkArtikel;
	}

	public Date getAktualisiert() {
		Date akt = this.aktualisiert;
		return akt;
	} 

	@JsonIgnore
	public int getAufLager() {
		return this.aufLager;
	}

	public void setAufLager(int aufLager) {
		this.aufLager = aufLager;
	}

	@JsonIgnore
	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public Date getErzeugt() {
		Date erz = this.erzeugt;
		return erz;
	}

	public String getKategorie() {
		return this.kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}
	
	public Artikel(String beschreibung, double preis) {
		this.beschreibung = beschreibung;
		this.preis = preis;
		this.aufLager += 1;
	}
	
	public Artikel(String beschreibung, double preis, String kategorie) {
		this.beschreibung = beschreibung;
		this.preis = preis;
		this.aufLager += 1;
		this.kategorie = kategorie;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Artikel other = (Artikel) obj;
		
		if (aufLager != other.aufLager) {
			return false;
		}
		
		if (beschreibung == null) {
			if (other.beschreibung != null) {
				return false;
			}
		}
		else if (!beschreibung.equals(other.beschreibung)) {
			return false;
		}
		
		if (Double.doubleToLongBits(preis) != Double.doubleToLongBits(other.preis)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		result = prime * result
				+ ((beschreibung == null) ? 0 : beschreibung.hashCode());
		long temp;
		temp = Double.doubleToLongBits(preis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	@Override
	public String toString() {
		return "Artikel [id=" + pkArtikel + ", beschreibung=" + beschreibung
		       + ", preis=" + preis + ", AufLager=" + aufLager
		       + ", erzeugt=" + erzeugt
			   + ", aktualisiert=" + aktualisiert + "]";
	}
	
}