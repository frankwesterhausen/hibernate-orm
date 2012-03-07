package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;

import org.hibernate.envers.SecondaryAuditTable;

public class XmlSecondaryAuditTable implements SecondaryAuditTable {

	private String secondaryTableName;
	private String secondaryAuditTableName;

	public Class<? extends Annotation> annotationType() {
		return SecondaryAuditTable.class;
	}

	public String secondaryTableName() {
		return secondaryTableName;
	}

	public String secondaryAuditTableName() {
		return secondaryAuditTableName;
	}

	public void setSecondaryTableName(String secondaryTableName) {
		this.secondaryTableName = secondaryTableName;
	}

	public void setSecondaryAuditTableName(String secondaryAuditTableName) {
		this.secondaryAuditTableName = secondaryAuditTableName;
	}

}
