package de.shop.kundenverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the ADRESSE database table.
 * 
 */
@Entity
@Table(name = "Adresse")
@XmlAccessorType(FIELD)
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_ADRESSE", unique = true, nullable = false, updatable = false)
	@XmlAttribute
	private long pkAdresse;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Column(name = "HAUSNR")
	@Min(1)
	@Max(1000)
	@XmlElement
	private String hausnr;

	@Column(name = "ORT")
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
	@XmlElement(required = true)
	private String ort;

	@Column(name = "PLZ")
	@Digits(integer = 5, fraction = 0)
	@XmlElement(required = true)
	private String plz;

	@Column(name = "STRASSE")
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
	@XmlElement
	private String strasse;

	@OneToOne
	@JoinColumn(name = "fk_kunde", nullable = false)
	@NotNull(message = "{kundeverwaltung.adresse.kunde.notNull}")
	@XmlTransient
	private Kunde kunde;

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public Adresse() {
		super();
	}
	
	public Adresse(String hausnr, String ort, String plz, String strasse) {
		this.hausnr = hausnr;
		this.ort = ort;
		this.plz = plz;
		this.strasse = strasse;	
	}
	
	public Adresse addKunde(Kunde kunde) {
		this.kunde = kunde;
		return this;
	}

	public long getPkAdresse() {
		return this.pkAdresse;
	}

	public void setPkAdresse(long pkAdresse) {
		this.pkAdresse = pkAdresse;
	}

	public Date getAktualisiert() {
		Date akt = this.aktualisiert;
		return akt;
	}

	public Date getErzeugt() {
		Date erz = this.erzeugt;
		return erz;
	}

	public String getHausnr() {
		return this.hausnr;
	}

	public void setHausnr(String hausnr) {
		this.hausnr = hausnr;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
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

}