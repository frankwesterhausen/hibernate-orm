package org.hibernate.envers.configuration.metadata.xml;

import java.util.List;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.envers.ModificationStore;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.configuration.metadata.reader.ClassAuditingData;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.EnversMappingType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.SecondaryAuditTable;
import org.hibernate.mapping.PersistentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to read the versioning meta-data from an Xml configuration file.<br>
 * Class which was used as a template is the AnnotationsMetadataReader.
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlMetadataReader {

	private final GlobalConfiguration globalCfg;
	private final ReflectionManager reflectionManager;
	private final PersistentClass pc;
	private final XmlAuditTable defaultAuditTable;

	/**
	 * The Xml-Mapping data
	 */
	private final EnversMappingType em;

	/**
	 * This object is filled with information read from annotations and returned
	 * by the <code>getVersioningData</code> method.
	 */
	private final ClassAuditingData auditData;

	/**
	 * Logger for Debug and Information Data.
	 */
	private static Logger Logger = LoggerFactory.getLogger( XmlMetadataReader.class );

	public XmlMetadataReader(GlobalConfiguration globalCfg, ReflectionManager reflectionManager, PersistentClass pc,
			EnversMappingType enversMapping) {
		Logger.debug( "Creating XmlMetdataReader for class '" + pc.getClassName() + "'" );
		this.globalCfg = globalCfg;
		this.reflectionManager = reflectionManager;
		this.pc = pc;
		em = enversMapping;
		auditData = new ClassAuditingData();
		defaultAuditTable = new XmlAuditTable();
		defaultAuditTable.setCatalog( "" );
		defaultAuditTable.setSchema( "" );
		defaultAuditTable.setValue( "" );
	}

	private ModificationStore getDefaultAudited(ClazzType clazz) {
		if ( clazz.getAudited() != null ) {
			return ModificationStore.FULL;
		}
		else {
			return null;
		}
	}

	private void addAuditTable(ClazzType clazz) {
		// JUST COPY THE ANNOTATION DATA
		if ( clazz.getAuditTable() == null ) {
			auditData.setAuditTable( defaultAuditTable );
		}
		else {
			XmlAuditTable xmlAuditTable = new XmlAuditTable();
			xmlAuditTable.setValue( clazz.getAuditTable().getValue() );
			xmlAuditTable.setCatalog( clazz.getAuditTable().getCatalog() );
			xmlAuditTable.setSchema( clazz.getAuditTable().getSchema() );
			auditData.setAuditTable( xmlAuditTable );
		}
	}

	private void addAuditSecondaryTables(ClazzType clazz) {
		// JUST COPY THE ANNOTATION DATA
		if ( clazz.getSecondaryAuditTable() != null ) {
			auditData.getSecondaryTableDictionary().put( clazz.getSecondaryAuditTable().getSecondaryTableName(),
					clazz.getSecondaryAuditTable().getSecondaryAuditTableName() );
		}
		if ( clazz.getSecondaryAuditTables() != null ) {
			for ( SecondaryAuditTable sat : clazz.getSecondaryAuditTables().getSecondaryAuditTable() ) {
				auditData.getSecondaryTableDictionary().put( sat.getSecondaryTableName(),
						sat.getSecondaryAuditTableName() );
			}
		}

	}

	/**
	 * Creates and returns the ClassAuditingData.
	 * 
	 * @return
	 */
	public ClassAuditingData getAuditData() {
		if ( pc.getClassName() == null ) {
			return auditData;
		}
		// READ DATA FROM XML
		String className = pc.getClassName();
		ClazzType entityClazz = XmlConfigurationTools.getClazzForName( className, em.getEntity() );
		if ( entityClazz == null ) {
			Logger.error( "No Configuration found for Class '" + className + "' in the Xml configuration !" );
			return null;
		}

		// TODO: IMPLEMENT THE MODIFICATION STORE FUNCTIONALITY
		ModificationStore defaultStore = getDefaultAudited( entityClazz );
		if ( defaultStore != null ) {
			auditData.setDefaultAudited( true );
		}

		// CREATE ENVERS META DATA
		new XmlAuditedPropertiesReader( defaultStore, new XmlPersistentClassPropertiesSource( entityClazz ),
				entityClazz, auditData, globalCfg, reflectionManager, "", em ).read();
		addAuditTable( entityClazz );
		addAuditSecondaryTables( entityClazz );

		return auditData;
	}

}
