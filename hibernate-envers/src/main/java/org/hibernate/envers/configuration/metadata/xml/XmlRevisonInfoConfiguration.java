package org.hibernate.envers.configuration.metadata.xml;

import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.configuration.metadata.AuditTableData;
import org.hibernate.envers.configuration.metadata.MetadataTools;
import org.hibernate.envers.configuration.metadata.RevisionInfoConfigurationResult;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.EnversMappingType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.PropertyType;
import org.hibernate.envers.entities.PropertyData;
import org.hibernate.envers.revisioninfo.DefaultRevisionInfoGenerator;
import org.hibernate.envers.revisioninfo.RevisionInfoGenerator;
import org.hibernate.envers.revisioninfo.RevisionInfoNumberReader;
import org.hibernate.envers.revisioninfo.RevisionInfoQueryCreator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xml Version of the RevisionInfoConfiguration Class.
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlRevisonInfoConfiguration {

	private static Logger Logger = LoggerFactory.getLogger( XmlRevisonInfoConfiguration.class );

	private String revisionInfoEntityName;
	private PropertyData revisionInfoIdData;
	private PropertyData revisionInfoTimestampData;
	private Type revisionInfoTimestampType;
	private GlobalConfiguration globalCfg;

	private String revisionPropType;
	private String revisionPropSqlType;

	private EnversMappingType em;

	public XmlRevisonInfoConfiguration(GlobalConfiguration globalCfg, EnversMappingType em) {
		this.globalCfg = globalCfg;
		revisionInfoEntityName = "org.hibernate.envers.DefaultRevisionEntity";
		revisionInfoIdData = new PropertyData( "id", "id", "field", null );
		revisionInfoTimestampData = new PropertyData( "timestamp", "timestamp", "field", null );
		revisionInfoTimestampType = new LongType();
		revisionPropType = "integer";
		this.em = em;
	}

	public RevisionInfoConfigurationResult configure(Configuration cfg, ReflectionManager reflectionManager) {
		RevisionInfoGenerator revisionInfoGenerator = null;
		ClazzType revisionEntity = XmlConfigurationTools.getUniqueRevisionEntity( em );
		Class<?> revisionInfoClass = null;
		if ( revisionEntity != null ) {
			this.checkRevisionEntityConfiguration( revisionEntity );
			PersistentClass pc = this.getPersistenClass( cfg, revisionEntity.getName() );
			revisionInfoEntityName = pc.getEntityName();
			revisionInfoClass = pc.getMappedClass();
			XClass clazz;
			try {
				clazz = reflectionManager.classForName( pc.getClassName(), this.getClass() );
			}
			catch ( ClassNotFoundException e ) {
				throw new MappingException( e );
			}
			this.setRevisionCfgProperties( clazz, reflectionManager, revisionEntity );
			revisionInfoTimestampType = pc.getProperty( revisionInfoTimestampData.getName() ).getType();
			revisionInfoGenerator = new DefaultRevisionInfoGenerator( revisionInfoEntityName, revisionInfoClass,
					this.getConfiguredRevisionListener( revisionEntity ), revisionInfoTimestampData,
					isTimestampAsDate() );

		}
		Document revisionInfoXmlMapping = null;
		if ( revisionInfoGenerator == null ) {
			revisionInfoClass = DefaultRevisionEntity.class;
			revisionInfoGenerator = new DefaultRevisionInfoGenerator( revisionInfoEntityName, revisionInfoClass,
					RevisionListener.class, revisionInfoTimestampData, isTimestampAsDate() );
			revisionInfoXmlMapping = generateDefaultRevisionInfoXmlMapping();
		}

		return new RevisionInfoConfigurationResult( revisionInfoGenerator, revisionInfoXmlMapping,
				new RevisionInfoQueryCreator( revisionInfoEntityName, revisionInfoIdData.getName(),
						revisionInfoTimestampData.getName(), isTimestampAsDate() ),
				generateRevisionInfoRelationMapping(), new RevisionInfoNumberReader( revisionInfoClass,
						revisionInfoIdData ), revisionInfoEntityName, revisionInfoClass, revisionInfoTimestampData );

	}

	/**
	 * This methods sets the global Revison configuration Variables. It should do something similar to the
	 * RevisionInfoConfiguration.searchForRevisionInfoCfgInProperties() method.
	 * 
	 * @param clazz
	 * @param reflectionManager
	 * @param revisionEntity
	 */
	private void setRevisionCfgProperties(XClass clazz, ReflectionManager reflectionManager, ClazzType revisionEntity) {
		// TODO: implement different access types and data types: default access type (property) and data type (integer)
		// is implemented
		String accessType = "property";
		PropertyType revisionIdProperty = this.getRevisionIdProperty( revisionEntity );
		PropertyType revisionTimestampProperty = this.getRevisionTimstampProperty( revisionEntity );
		revisionInfoIdData = new PropertyData( revisionIdProperty.getName(), revisionIdProperty.getName(), accessType,
				null );
		revisionInfoTimestampData = new PropertyData( revisionTimestampProperty.getName(),
				revisionTimestampProperty.getName(), accessType, null );
		// TODO: HANDLE THE REVISION_PROP_COLUMN -> see RevisionInfoConfiguration.searchForRevisionInfoCfgInProperties()
	}

	/**
	 * Checks if the RevisionEntity has exact one RevicisonId Property and exact one RevisionTimeStamp Property
	 * 
	 * @param revisionEntity
	 */
	public void checkRevisionEntityConfiguration(ClazzType revisionEntity) {
		boolean revIdPropFound = false;
		boolean revTimestampPropFound = false;
		for ( PropertyType property : revisionEntity.getProperty() ) {
			if ( property.getRevisionNumber() != null ) {
				if ( revIdPropFound )
					throw new MappingException( "Only one property may be configured as RevisionId !" );
				revIdPropFound = true;
			}
			else if ( property.getRevisionTimestamp() != null ) {
				if ( revTimestampPropFound )
					throw new MappingException( "Only one property may be configured as RevisionTimestamp !" );
				revTimestampPropFound = true;
			}
		}
		if ( !revIdPropFound )
			throw new MappingException( "The RevisionEntity has no configured RevisionId attribute !" );
		if ( !revTimestampPropFound )
			throw new MappingException( "The RevisionEntity has no configured RevisionTimestamp attribute !" );
	}

	/**
	 * Gets the PersistenClass from the HibernateConfiguration
	 * 
	 * @param cfg
	 *            the HibernateConfiguration
	 * @param className
	 *            the name of the class
	 * @return
	 */
	private PersistentClass getPersistenClass(Configuration cfg, String className) {
		Iterator<PersistentClass> classes = (Iterator<PersistentClass>) cfg.getClassMappings();
		while ( classes.hasNext() ) {
			PersistentClass pc = classes.next();
			Logger.debug( "Persisten Class: '" + pc.getClassName() + "'" );
			if ( pc.getClassName().equals( className ) )
				return pc;
		}
		throw new MappingException( "The Class '" + className + "' can not be found in the Hibernate-Mappings !" );
	}

	/**
	 * Gets the RevisionIdProperty from the RevisionEntity
	 * 
	 * @param clazz
	 * @return
	 */
	private PropertyType getRevisionIdProperty(ClazzType clazz) {
		PropertyType revisionIdProperty = null;
		for ( PropertyType property : clazz.getProperty() ) {
			if ( property.getRevisionNumber() != null ) {
				if ( revisionIdProperty != null )
					throw new MappingException(
							"More then one RevisionId property is configured in the RevisionEntity !" );
				revisionIdProperty = property;
			}
		}
		if ( revisionIdProperty != null )
			return revisionIdProperty;
		throw new MappingException( "Couldn't find an RevisionId property in the RevisionEntity !" );
	}

	/**
	 * Gets the RevisionTimstampProperty from the RevisionEntity
	 * 
	 * @param clazz
	 * @return
	 */
	private PropertyType getRevisionTimstampProperty(ClazzType clazz) {
		PropertyType revisionTimstampsProperty = null;
		for ( PropertyType property : clazz.getProperty() ) {
			if ( property.getRevisionTimestamp() != null ) {
				if ( revisionTimstampsProperty != null )
					throw new MappingException(
							"More then one Timestamp property is configured in the RevisionEntity !" );
				revisionTimstampsProperty = property;
			}
		}
		if ( revisionTimstampsProperty != null )
			return revisionTimstampsProperty;
		throw new MappingException( "Couldn't find an RevisionTimstamp property in the RevisionEntity !" );
	}

	/**
	 * Code from RevisionInfoConfiguration class
	 * 
	 * @return
	 */
	private boolean isTimestampAsDate() {
		String typename = revisionInfoTimestampType.getName();
		return "date".equals( typename ) || "time".equals( typename ) || "timestamp".equals( typename );
	}

	/**
	 * Code from RevisionInfoConfiguration class
	 * 
	 * @return
	 */
	private Document generateDefaultRevisionInfoXmlMapping() {
		Document document = DocumentHelper.createDocument();

		Element class_mapping = MetadataTools.createEntity( document,
				new AuditTableData( null, null, globalCfg.getDefaultSchemaName(), globalCfg.getDefaultCatalogName() ),
				null );

		class_mapping.addAttribute( "name", revisionInfoEntityName );
		class_mapping.addAttribute( "table", "REVINFO" );

		Element idProperty = MetadataTools.addNativelyGeneratedId( class_mapping, revisionInfoIdData.getName(),
				revisionPropType );
		MetadataTools.addColumn( idProperty, "REV", null, 0, 0, null, null, null, false );

		Element timestampProperty = MetadataTools.addProperty( class_mapping, revisionInfoTimestampData.getName(),
				revisionInfoTimestampType.getName(), true, false );
		MetadataTools.addColumn( timestampProperty, "REVTSTMP", null, 0, 0, null, null, null, false );

		return document;
	}

	/**
	 * Code from RevisionInfoConfiguration class
	 * 
	 * @return
	 */
	private Element generateRevisionInfoRelationMapping() {
		Document document = DocumentHelper.createDocument();
		Element rev_rel_mapping = document.addElement( "key-many-to-one" );
		rev_rel_mapping.addAttribute( "type", revisionPropType );
		rev_rel_mapping.addAttribute( "class", revisionInfoEntityName );

		if ( revisionPropSqlType != null ) {
			// Putting a fake name to make Hibernate happy. It will be replaced
			// later anyway.
			MetadataTools.addColumn( rev_rel_mapping, "*", null, 0, 0, revisionPropSqlType, null, null, false );
		}

		return rev_rel_mapping;
	}

	/**
	 * Gets the RevisionListener. Loads the configured RevisionListener or returns the default if there is no
	 * configured.
	 * 
	 * @param revisionEntity
	 * @return
	 */
	private Class<? extends RevisionListener> getConfiguredRevisionListener(ClazzType revisionEntity) {
		if ( revisionEntity.getRevisionEntity().getRevisionListener() == null
				|| revisionEntity.getRevisionEntity().getRevisionListener().equals( "" ) ) {
			return RevisionListener.class;
		}
		// TODO: IS THAT THE RIGHT WAY TO LOAD THE CLASS ???
		try {
			Class<? extends RevisionListener> revisionListener = (Class<? extends RevisionListener>) Class
					.forName( revisionEntity.getRevisionEntity().getRevisionListener().getValue() );
			return revisionListener;
		}
		catch ( ClassNotFoundException e ) {
			throw new MappingException( "Couldn't load the RevisionListener !", e );
		}

	}

}
