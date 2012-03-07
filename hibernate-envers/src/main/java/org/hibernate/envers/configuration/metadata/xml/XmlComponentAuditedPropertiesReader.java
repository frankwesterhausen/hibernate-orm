package org.hibernate.envers.configuration.metadata.xml;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.envers.ModificationStore;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.configuration.metadata.reader.AuditedPropertiesHolder;
import org.hibernate.envers.configuration.metadata.reader.PersistentPropertiesSource;
import org.hibernate.envers.configuration.metadata.reader.PropertyAuditingData;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.EnversMappingType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.PropertyType;

/**
 * TODO: NOT USED IN THE MOMENT: USE FOR INHERITANNCE IMPLEMENTATION OF THE COMPONENT ELEMENT
 * 
 * @author Frank
 * 
 */
public class XmlComponentAuditedPropertiesReader extends XmlAuditedPropertiesReader {

	public XmlComponentAuditedPropertiesReader(ModificationStore defaultStore,
			PersistentPropertiesSource persistentPropertiesSource, ClazzType clazz,
			AuditedPropertiesHolder auditedPropertiesHolder, GlobalConfiguration globalCfg,
			ReflectionManager reflectionManager, String propertyNamePrefix, EnversMappingType em) {
		super( defaultStore, persistentPropertiesSource, clazz, auditedPropertiesHolder, globalCfg, reflectionManager,
				propertyNamePrefix, em );
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkAudited(PropertyType property, PropertyAuditingData propertyAuditingData, ClazzType clazz) {
		// TODO: CROSS CHECK WITH ORIGINAL: THERE EVERYTHING IS AUDITED IN AN COMPONENT ???
		if ( property.getAudited() != null ) {
			propertyAuditingData.setStore( ModificationStore.valueOf( property.getAudited().getModStore() ) );
			propertyAuditingData.setRelationTargetAuditMode( RelationTargetAuditMode.valueOf( property.getAudited()
					.getRelationTargetAuditMode() ) );
			return true;
		}
		else if ( clazz.getAudited() != null ) {
			propertyAuditingData.setStore( ModificationStore.valueOf( clazz.getAudited().getModStore() ) );
			propertyAuditingData.setRelationTargetAuditMode( RelationTargetAuditMode.valueOf( clazz.getAudited()
					.getRelationTargetAuditMode() ) );
			return true;
		}
		else {
			return false;
		}

	}

}
