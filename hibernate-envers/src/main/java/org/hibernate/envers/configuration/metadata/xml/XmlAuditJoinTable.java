package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;
import javax.persistence.JoinColumn;
import org.hibernate.envers.AuditJoinTable;

/**
 * Class to implement the Annotations on which Envers is based normaly.
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlAuditJoinTable implements AuditJoinTable {

	private String name;
	private String schema;
	private String catalog;
	private JoinColumn[] inverseJoinColumns;

	
	public Class<? extends Annotation> annotationType() {
		return AuditJoinTable.class;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setInverseJoinColumns(JoinColumn[] inverseJoinColumns) {
		this.inverseJoinColumns = inverseJoinColumns;
	}

	
	public JoinColumn[] inverseJoinColumns() {
		return inverseJoinColumns;
	}

}
