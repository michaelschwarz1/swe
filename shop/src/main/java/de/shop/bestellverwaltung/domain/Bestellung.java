package de.shop.bestellverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sun.istack.NotNull;

import de.shop.kundenverwaltung.domain.Kunde;


/**
 * The persistent class for the BESTELLUNG database table.
 * 
 */
@Entity

@Table(name = "Bestellung")
@NamedQueries({ 
	@NamedQuery(
			name = Bestellung.ALLE_BESTELLUNGEN,
			query = "SELECT b FROM Bestellung b"
			),		
	@NamedQuery(
			name = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
			query = "SELECT b FROM   Bestellung b WHERE  b.kunde.id =:" + Bestellung.PARAM_KUNDEID
			),
	@NamedQuery(
			name = Bestellung.FIND_BESTELLUNGEN_BY_NACHNAME,
			query = "SELECT b FROM Bestellung b WHERE b.kunde.nachname =:" + Bestellung.PARAM_KUNDE_NACHNAME
			)
	
			
})

@XmlRootElement
public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX + "findBestellungenByKunde";
	public static final String FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN = 
			PREFIX + "findBestellungenByIdFetchLieferungen";
	public static final String FIND_BESTELLUNGEN_BY_NACHNAME = PREFIX + "findBestellungenByNachname";
	public static final String FIND_KUNDE_BY_ID = PREFIX + "findBestellungKundeById";
	public static final String ALLE_BESTELLUNGEN = PREFIX + "allebestellungen";
	public static final String ALLE_POSITIONEN_FUER_BESTELLUNGEN = PREFIX + "allesPositionenfuerartikel";
	public static final String PARAM_KUNDE_NACHNAME = "Schwarzz";
	public static final String PARAM_KUNDEID = "kundeId";
	public static final String PARAM_ID = "id";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_BESTELLUNG", unique = true, nullable = false, updatable = false)
	@XmlAttribute
	private Long pkBestellung;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt;

	@Column(nullable = false, insertable = true, updatable = true)
	@NotNull
	private int idx;

	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_KUNDE", nullable = false, insertable = true, updatable = false)
	@XmlTransient
	private Kunde kunde;
	
	@Transient
	@XmlElement(name = "kunde", required = true)
	private URI kundeUri;

	public URI getKundeUri() {
		return kundeUri;
	}
	
	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}

	@OneToMany
	@JoinColumn(name = "FK_BESTELLUNG", nullable = false, insertable = true, updatable = false)
	@OrderColumn(name = "idx")
	private List<Position> positionen;

	public Bestellung() {
		super();
	}
	
	public Bestellung(List <Position> position) {
		super();
		this.positionen = position;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert =  new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert =  new Date();
	}
	
	public Long getPkBestellung() {
		return this.pkBestellung;
	}

	public void setPkBestellung(Long pkBestellung) {
		this.pkBestellung = pkBestellung;
	}										

	public Date getErzeugt() {
		Date erz = this.erzeugt;
		return erz;
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public List<Position> getPositionen() {
		return this.positionen;
	}

	public void setPositionen(List<Position> position) {
		this.positionen = position;
	}
	
	public Bestellung addPosition(Position p) {
		if (positionen == null)
			positionen = new ArrayList<Position>();
		
		positionen.add(p);
		return this;
	}
}