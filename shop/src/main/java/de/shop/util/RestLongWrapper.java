package de.shop.util;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "long")
@XmlAccessorType(FIELD)
public class RestLongWrapper {
	@XmlValue
	private Long value;

	public RestLongWrapper() {
		super();
	}

	public RestLongWrapper(Long value) {
		super();
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}
