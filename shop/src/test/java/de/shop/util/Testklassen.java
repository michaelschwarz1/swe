package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.kundenverwaltung.domain.KundeTest;

public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus *VERSCHIEDENEN* Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	private List<?extends Class<? extends AbstractTest>> classes = Arrays.asList(KundeTest.class);
	
	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<?extends Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
