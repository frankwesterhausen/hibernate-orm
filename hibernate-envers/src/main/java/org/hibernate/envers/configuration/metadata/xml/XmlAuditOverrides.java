package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;

;

public class XmlAuditOverrides implements AuditOverrides {

	AuditOverride auditOverrides[];

	public Class<? extends Annotation> annotationType() {
		return AuditOverrides.class;
	}

	public void setAuditOverrides(AuditOverride[] auditOverrides) {
		this.auditOverrides = auditOverrides;
	}

	public AuditOverride[] value() {
		return auditOverrides;
	}

}
