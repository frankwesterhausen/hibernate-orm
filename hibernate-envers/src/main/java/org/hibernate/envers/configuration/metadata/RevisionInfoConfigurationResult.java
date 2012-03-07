package org.hibernate.envers.configuration.metadata;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.envers.entities.PropertyData;
import org.hibernate.envers.revisioninfo.RevisionInfoGenerator;
import org.hibernate.envers.revisioninfo.RevisionInfoNumberReader;
import org.hibernate.envers.revisioninfo.RevisionInfoQueryCreator;

public class RevisionInfoConfigurationResult {
	private final RevisionInfoGenerator revisionInfoGenerator;
	private final Document revisionInfoXmlMapping;
	private final RevisionInfoQueryCreator revisionInfoQueryCreator;
	private final Element revisionInfoRelationMapping;
	private final RevisionInfoNumberReader revisionInfoNumberReader;
	private final String revisionInfoEntityName;
	private final Class<?> revisionInfoClass;
	private final PropertyData revisionInfoTimestampData;

	public RevisionInfoConfigurationResult(RevisionInfoGenerator revisionInfoGenerator,
			Document revisionInfoXmlMapping, RevisionInfoQueryCreator revisionInfoQueryCreator,
			Element revisionInfoRelationMapping, RevisionInfoNumberReader revisionInfoNumberReader,
			String revisionInfoEntityName, Class<?> revisionInfoClass, PropertyData revisionInfoTimestampData) {
		this.revisionInfoGenerator = revisionInfoGenerator;
		this.revisionInfoXmlMapping = revisionInfoXmlMapping;
		this.revisionInfoQueryCreator = revisionInfoQueryCreator;
		this.revisionInfoRelationMapping = revisionInfoRelationMapping;
		this.revisionInfoNumberReader = revisionInfoNumberReader;
		this.revisionInfoEntityName = revisionInfoEntityName;
		this.revisionInfoClass = revisionInfoClass;
		this.revisionInfoTimestampData = revisionInfoTimestampData;
	}

	public RevisionInfoGenerator getRevisionInfoGenerator() {
		return revisionInfoGenerator;
	}

	public Document getRevisionInfoXmlMapping() {
		return revisionInfoXmlMapping;
	}

	public RevisionInfoQueryCreator getRevisionInfoQueryCreator() {
		return revisionInfoQueryCreator;
	}

	public Element getRevisionInfoRelationMapping() {
		return revisionInfoRelationMapping;
	}

	public RevisionInfoNumberReader getRevisionInfoNumberReader() {
		return revisionInfoNumberReader;
	}

	public String getRevisionInfoEntityName() {
		return revisionInfoEntityName;
	}

	public Class<?> getRevisionInfoClass() {
		return revisionInfoClass;
	}

	public PropertyData getRevisionInfoTimestampData() {
		return revisionInfoTimestampData;
	}

}

