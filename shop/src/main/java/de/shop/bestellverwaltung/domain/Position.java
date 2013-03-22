package de.shop.bestellverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.Transient;
/*import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;*/
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Artikel;


/**
 * The persistent class for the "POSITION" database table.
 * 
 */
@Entity
@Table(name = "position")

/*@NamedQueries({
@NamedQuery(name  = Position.FIND_LADENHUETER,
	            query = "SELECT a"
	            		+" FROM Artikel a"
	            	    +" WHERE a NOT IN (SELECT p.artikel FROM Position p)"),

 @NamedQuery(
		name = Position.SUCHE_BESTELLPOSITION,
		query = "select p from Position p where p.PK_POSITION = :pkPosition")
		
})*/

@XmlRootElement
public class Position implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	private static final String PREFIX = "Position.";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";
	public static final String SUCHE_BESTELLPOSITION = PREFIX + "suchebestellposition";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_POSITION", unique = true, nullable = false, updatable = false)
	@XmlAttribute
	private Long pkPosition;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;

	@Column(name = "ANZAHL")
	@Min(0)
	@XmlElement
	private int anzahl;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_ARTIKEL", nullable = false)
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel", required = true)
	private URI artikelUri;
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	//bi-directional many-to-one association to Bestellung
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_BESTELLUNG", nullable = false, insertable = false, updatable = false)
	@OrderColumn(name = "idx")
	@XmlTransient
	private Bestellung bestellung;

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	public Position() {
		super();
	}
	public Position(Artikel a) {
		this.artikel = a;
		this.anzahl = 1;
	}
	public Position(Artikel a, int anzahl) {
		this.artikel = a;
		this.anzahl = anzahl;
	}

	public Long getPkPosition() {
		return this.pkPosition;
	}

	public void setPkPosition(Long pkPosition) {
		this.pkPosition = pkPosition;
	}
	
	public URI getArtikelUri() {
		return artikelUri;
	}

	public Date getAktualisiert() {
		Date akt = this.aktualisiert;
		return akt;
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	public Bestellung getBestellung() {
		return this.bestellung;
	}

	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}
	
}