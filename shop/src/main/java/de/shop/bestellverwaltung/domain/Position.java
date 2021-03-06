package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
/*import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;*/
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

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

public class Position implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	private static final String PREFIX = "Position.";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";
	public static final String SUCHE_BESTELLPOSITION = PREFIX + "suchebestellposition";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_POSITION", unique = true, nullable = false, updatable = false)
	@JsonProperty
	private Long pkPosition = null;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(name = "ANZAHL")
	@Min(0)
	@JsonProperty
	private int anzahl;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_ARTIKEL", nullable = false)
	@JsonIgnore
	private Artikel artikel;
	
	@Transient
	@JsonProperty
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
	@JsonIgnore
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
		final Date akt = this.aktualisiert;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
