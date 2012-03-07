package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;

import org.hibernate.envers.AuditTable;

/**
 * Class to implement the Annotations on which Envers is based normaly.
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlAuditTable implements AuditTable {

	private String value;
	private String schema;
	private String catalog;

	public Class<? extends Annotation> annotationType() {
		return AuditTable.class;
	}

	
	public String value() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String schema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String catalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

}
