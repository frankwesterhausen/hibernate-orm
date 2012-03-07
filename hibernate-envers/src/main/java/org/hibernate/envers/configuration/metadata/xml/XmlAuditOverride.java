package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditOverride;

public class XmlAuditOverride implements AuditOverride {

	private String name;
	private boolean isAudited;
	private XmlAuditJoinTable xmlAuditJoinTable;

	public Class<? extends Annotation> annotationType() {
		return AuditOverride.class;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void setAudited(boolean isAudited) {
		this.isAudited = isAudited;
	}

	public boolean isAudited() {
		return isAudited;
	}

	public void setXmlAuditJoinTable(XmlAuditJoinTable xmlAuditJoinTable) {
		this.xmlAuditJoinTable = xmlAuditJoinTable;
	}

	public AuditJoinTable auditJoinTable() {
		return xmlAuditJoinTable;
	}

}
