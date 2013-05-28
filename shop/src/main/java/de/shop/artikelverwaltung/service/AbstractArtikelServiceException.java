package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public class AbstractArtikelServiceException extends AbstractShopException {
	private static final long serialVersionUID = -6210914897148515017L;

	public AbstractArtikelServiceException(String msg) {
		super(msg);
	}
	
	public AbstractArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
