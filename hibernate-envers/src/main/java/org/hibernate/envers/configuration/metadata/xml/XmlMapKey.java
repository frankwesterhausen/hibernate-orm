package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;

import javax.persistence.MapKey;

public class XmlMapKey implements MapKey {

	private String name;

	public Class<? extends Annotation> annotationType() {
		return MapKey.class;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

}
