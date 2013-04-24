package de.shop.kundenverwaltung.domain;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sun.istack.NotNull;

import de.shop.auth.service.jboss.AuthService.RolleType;
import de.shop.bestellverwaltung.domain.Bestellung;


/**
 * The persistent class for the KUNDE database table.
 * 
 */
@Entity
@Table(name = "Kunde")
@Inheritance 
@NamedQueries({
	
	@NamedQuery(name  = Kunde.FIND_KUNDEN,
                query = "SELECT k"
				        + " FROM   Kunde k"),			       
	@NamedQuery(name  = Kunde.FIND_KUNDR_BY_ID,
		        query = "SELECT   k"
		                + " FROM  Kunde k"
		                + " WHERE k.id LIKE :" + Kunde.PARAM_KUNDE_ID),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME,
	            query = "SELECT k"
				        + " FROM   Kunde k"
	            		+ " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
 	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL,
   	            query = "SELECT DISTINCT k"
   			            + " FROM   Kunde k"
   			            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),
   	@NamedQuery(name = Kunde.FIND_KUNDEN_BY_PLZ,
   				query ="SELECT k"
   						+ " FROM Kunde k"
   						+ " WHERE k.adresse.plz LIKE :" + Kunde.PARAM_KUNDE_ADRESSE_PLZ)
})

@RequestScoped
public class Kunde implements Serializable {
 private static final long serialVersionUID = 5685115602958386843L;

private static final String PREFIX = "Kunde.";
public static final String FIND_KUNDEN = PREFIX + "findKunden";
public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX + "findNachnamenByPrefix";
public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
public static final String FIND_KUNDEN_BY_DATE = PREFIX + "findKundenByDate";
public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
public static final String FIND_KUNDR_BY_ID = PREFIX + "findKundeById";
public static final String PARAM_KUNDE_ID = "kundeId";
public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
public static final String PARAM_KUNDE_NACHNAME = "nachname";
public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
public static final String PARAM_KUNDE_SEIT = "seit";
public static final String PARAM_KUNDE_EMAIL = "email";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PK_KUNDE", unique = true, nullable = false, updatable = false)
	@JsonProperty
	private Long pkKunde;

	@Column(name = "NACHNAME")
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
	@JsonProperty
	private String nachname;
	
	@Column(name = "VORNAME")
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
	@JsonProperty
	private String vorname;
	
	@Column(name = "EMAIL")
	@NotNull
	@Size(max = 128)
	@Pattern(regexp = "[\\w.%-]+@[\\w.%-]+\\.[A-Za-z]{2,4}")
	@JsonProperty
	private String email;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;
	
	@Column(name = "PASSWORD")
	@JsonIgnore
	private String password;
	
	@Transient
	@JsonProperty
	private URI bestellungenUri;

	//bi-directional many-to-one association to Bestellung
	@OneToMany()
	@JoinColumn(name = "FK_KUNDE", nullable = true)
	@Size(min = 1)
	@JsonIgnore
	private List<Bestellung> bestellung;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "fk_kunde", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "fk_kunde", "fk_rolle" }))
	@Column(table = "kunde_rolle", name = "fk_rolle", nullable = false)
	private Set<RolleType> rollen;

	@OneToOne(cascade = {PERSIST, REMOVE }, mappedBy = "kunde") //(optional =false)
	@NotNull
	@Valid
	@JsonProperty
	private Adresse adresse;
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	public Kunde(String nachname, String vorname, String email, String password, Adresse adresse) {
		this.nachname = nachname;
		this.vorname = vorname;
		this.email = email;
		this.password = password;
		this.adresse = adresse;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}

	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}
	
	public Kunde() {
		super();
	}
	
	public void setValues(Kunde k) {
		nachname = k.nachname;
		vorname = k.vorname;
		email = k.email;
		password = k.password;
	}
	
	public Long getPkKunde() {
		return this.pkKunde;
	}

	public void setPkKunde(Long pkKunde) {
		this.pkKunde = pkKunde;
	}

	public Date getAktualisiert() {
		Date akt = this.aktualisiert;
		return akt;
	}

	public Date getErzeugt() {
		Date erz = this.erzeugt;
		return erz;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public List<Bestellung> getBestellung() {
		return this.bestellung;
	}

	public void setBestellung(List<Bestellung> bestellung) {
		this.bestellung = bestellung;
	}

	public Adresse getAdresse() {
		return this.adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	
	public Kunde add(Kunde k) {
		
		if (pkKunde.equals(k.pkKunde))
			return new Kunde(this.vorname, this.nachname,  this.email, this.password , this.adresse);
		return null;	
	}

	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}

}