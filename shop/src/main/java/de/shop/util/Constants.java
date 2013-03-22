package de.shop.util;

public final class Constants {
	// JPA
	public static final Long KEINE_ID = null;
	public static final long MIN_ID = 1L;
	public static final int INT_ANZ_ZIFFERN = 11;
	public static final int LONG_ANZ_ZIFFERN = 20;
	
	// JNDI-NAME fuer UserTransaction
	public static final String TRANSACTION_NAME = "java:jboss/UserTransaction";
	
	// REST
	public static final String ARTIKELVERWALTUNG_NS = "urn:shop:artikelverwaltung";
	public static final String BESTELLVERWALTUNG_NS = "urn:shop:bestellverwaltung";
	public static final String KUNDENVERWALTUNG_NS = "urn:shop:kundenverwaltung";
	
	private Constants() {
	}
}
