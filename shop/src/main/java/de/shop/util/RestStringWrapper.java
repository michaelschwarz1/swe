package de.shop.util;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "string")
@XmlAccessorType(FIELD)
public class RestStringWrapper {
	@XmlValue
	private String value;

	public RestStringWrapper() {
		super();
	}

	public RestStringWrapper(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
