package org.hibernate.envers.configuration.metadata.xml;

import static org.hibernate.envers.tools.Tools.newHashSet;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.JoinColumn;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.ModificationStore;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.configuration.metadata.reader.AuditedPropertiesHolder;
import org.hibernate.envers.configuration.metadata.reader.ComponentAuditingData;
import org.hibernate.envers.configuration.metadata.reader.PersistentPropertiesSource;
import org.hibernate.envers.configuration.metadata.reader.PropertyAuditingData;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditMappedBy;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditOverride;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditOverrides;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditParent;
import org.hibernate.envers.configuration.metadata.xml.jaxb.Audited;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ComponentType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.EnversMappingType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.MapKey;
import org.hibernate.envers.configuration.metadata.xml.jaxb.PropertyType;
import org.hibernate.envers.tools.MappingTools;
import org.hibernate.mapping.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads persistent properties from an Clazz Element and adds the ones that are audited to an AuditedPropertiesHolder.<br>
 * Prototype Status !!!
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlAuditedPropertiesReader {

	private final EnversMappingType em;

	protected final ModificationStore defaultStore;
	private final PersistentPropertiesSource persistentPropertiesSource;
	private final AuditedPropertiesHolder auditedPropertiesHolder;
	private final GlobalConfiguration globalCfg;
	private final ReflectionManager reflectionManager;
	private final String propertyNamePrefix;

	private final Set<String> propertyAccessedPersistentProperties;
	private final Set<String> fieldAccessedPersistentProperties;

	/**
	 * Class for which the properties are read.
	 */
	private final ClazzType clazz;

	private static Logger Logger = LoggerFactory.getLogger( XmlAuditedPropertiesReader.class );

	public XmlAuditedPropertiesReader(ModificationStore defaultStore,
			PersistentPropertiesSource persistentPropertiesSource, ClazzType clazz,
			AuditedPropertiesHolder auditedPropertiesHolder, GlobalConfiguration globalCfg,
			ReflectionManager reflectionManager, String propertyNamePrefix, EnversMappingType em) {
		this.defaultStore = defaultStore;
		this.persistentPropertiesSource = persistentPropertiesSource;
		this.auditedPropertiesHolder = auditedPropertiesHolder;
		this.globalCfg = globalCfg;
		this.reflectionManager = reflectionManager;
		this.propertyNamePrefix = propertyNamePrefix;
		this.clazz = clazz;
		this.em = em;

		propertyAccessedPersistentProperties = newHashSet();
		fieldAccessedPersistentProperties = newHashSet();
	}

	public void read() {
		Logger.info( "Reading Xml-Metadata" );
		readPersistentPropertiesAccess();

		// RETRIEVE SUPER CLASSES FOR AUDITING
		Set<ClazzType> declaredAuditedSuperclasses = new HashSet<ClazzType>();
		doGetDeclaredAuditedSuperClasses( clazz, declaredAuditedSuperclasses );

		// ADDING PROPERTIES
		addPropertiesFromClass( clazz, declaredAuditedSuperclasses );

	}

	private Audited computeAuditConfiguration(ClazzType clazz, Set<ClazzType> declaredAuditedSuperclasses) {
		Audited audited = clazz.getAudited();
		if ( audited == null && declaredAuditedSuperclasses.contains( clazz ) ) {
			// TODO: WHAT HAS TO BE DONE HERE ???
		}
		return null;
	}

	private void addPropertiesFromClass(ClazzType clazz, Set<ClazzType> declaredAuditedSuperclasses) {
		// TODO: IMPLEMENT THE SUPER-CLASS AND COMPONENT FUNCTIONALITY: LOOK IN THE AuditedPropertiesReader-Class
		Audited audited = computeAuditConfiguration( clazz, declaredAuditedSuperclasses );

		addFromProperties( clazz, "field", fieldAccessedPersistentProperties );
		addFromProperties( clazz, "property", fieldAccessedPersistentProperties );

		if ( audited != null && clazz.getParent() != null ) {
			addPropertiesFromClass( XmlConfigurationTools.getClazzForName( clazz.getParent(), em.getEntity() ),
					declaredAuditedSuperclasses );
		}
	}

	private void addFromProperties(ClazzType clazz, String accessType, Set<String> persistentProperties) {

		List<PropertyType> propertyList = clazz.getProperty();
		for ( PropertyType property : propertyList ) {
			if ( property.getPropertyAccessorName().equals( accessType )
					&& ( !auditedPropertiesHolder.contains( property.getName() ) ) ) {
				addFromNotComponentProperty( property, accessType, clazz );
			}
		}
		List<ComponentType> componentList = clazz.getComponent();
		for ( ComponentType component : componentList ) {
			if ( component.getProperty().getPropertyAccessorName().equals( accessType )
					&& ( !auditedPropertiesHolder.contains( component.getProperty().getName() ) ) ) {
				addFromComponentProperty( component, accessType, clazz );
			}
		}
	}

	private void addFromComponentProperty(ComponentType component, String accessType, ClazzType clazz) {
		ComponentAuditingData componentData = new ComponentAuditingData();
		boolean isAudited = fillPropertyData( component.getProperty(), componentData, clazz );

		XmlComponentAuditedPropertiesReader xmlComponentAuditedPropertiesReader = new XmlComponentAuditedPropertiesReader(
				ModificationStore.FULL, null, component.getEntity(), componentData, globalCfg, reflectionManager,
				propertyNamePrefix + MappingTools.createComponentPrefix( component.getProperty().getName() ), em );
		xmlComponentAuditedPropertiesReader.read();

		if ( isAudited )
			auditedPropertiesHolder.addPropertyAuditingData( component.getProperty().getName(), componentData );
	}

	private void addFromNotComponentProperty(PropertyType property, String accessType, ClazzType clazz) {
		PropertyAuditingData propertyAuditingData = new PropertyAuditingData();
		boolean isAudited = fillPropertyData( property, propertyAuditingData, clazz );
		if ( isAudited )
			auditedPropertiesHolder.addPropertyAuditingData( property.getName(), propertyAuditingData );
	}

	/**
	 * Checks if a property is audited and if yes, fills all of its data.
	 * 
	 * @param property
	 * @param propertyAuditingData
	 * @return
	 */
	private boolean fillPropertyData(PropertyType property, PropertyAuditingData propertyAuditingData, ClazzType clazz) {
		if ( property.getNotAudited() != null )
			return false;
		// CHECK THE OPTIMISTIC LOCKING AUDITING
		if ( globalCfg.isDoNotAuditOptimisticLockingField() ) {
			if ( property.getVersion() != null ) {
				return false;
			}
		}

		if ( !this.checkAudited( property, propertyAuditingData, clazz ) )
			return false;
		propertyAuditingData.setName( propertyNamePrefix + property.getName() );
		propertyAuditingData.setBeanName( property.getName() );
		propertyAuditingData.setAccessType( property.getPropertyAccessorName() );
		addPropertyJoinTables( property, propertyAuditingData );
		addPropertyAuditingOverrides( property, propertyAuditingData );
		if ( !processPropertyAuditingOverrides( property, propertyAuditingData ) ) {
			return false; // not audited due to AuditOverride annotation
		}
		addPropertyMapKey( property, propertyAuditingData );
		setPropertyAuditMappedBy( property, propertyAuditingData );
		return true;
	}

	protected boolean checkAudited(PropertyType property, PropertyAuditingData propertyAuditingData, ClazzType clazz) {
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

	private void setPropertyAuditMappedBy(PropertyType property, PropertyAuditingData propertyAuditingData) {
		AuditMappedBy auditMappedBy = property.getAuditMappedBy();
		if ( auditMappedBy != null ) {
			propertyAuditingData.setAuditMappedBy( auditMappedBy.getMappedBy() );
			if ( !"".equals( auditMappedBy.getPositionMappedBy() ) ) {
				propertyAuditingData.setPositionMappedBy( auditMappedBy.getPositionMappedBy() );
			}
		}
	}

	private void addPropertyMapKey(PropertyType property, PropertyAuditingData propertyAuditingData) {
		MapKey mapKey = property.getMapKey();
		if ( mapKey != null ) {
			propertyAuditingData.setMapKey( mapKey.getName() );
		}

	}

	private void addPropertyJoinTables(PropertyType property, PropertyAuditingData propertyAuditingData) {
		// TODO: COMPARE WITH METHOD IN AuditedPropertiesReader-Class
		propertyAuditingData.setJoinTable( DEFAULT_AUDIT_JOIN_TABLE );
	}

	private void addPropertyAuditingOverrides(PropertyType property, PropertyAuditingData propertyAuditingData) {
		AuditOverride auditOverride = property.getAuditOverride();
		if ( auditOverride != null ) {
			XmlAuditOverride xmlAuditOverride = XmlConfigurationTools.transferToXmlAudtOverride( auditOverride );
			propertyAuditingData.addAuditingOverride( xmlAuditOverride );
		}
		AuditOverrides auditOverrides = property.getAuditOverrides();
		if ( auditOverrides != null ) {
			XmlAuditOverrides xmlAuditOverrides = XmlConfigurationTools.transferToXmlAudtOverrides( auditOverrides );
			propertyAuditingData.addAuditingOverrides( xmlAuditOverrides );
		}
	}

	private boolean processPropertyAuditingOverrides(PropertyType property, PropertyAuditingData propertyAuditingData) {
		// TODO: JUST COPIED AND WORKED WITH THE ANNOTATIONS, DOES THAT WORK ?
		if ( this.auditedPropertiesHolder instanceof ComponentAuditingData ) {
			List<org.hibernate.envers.AuditOverride> overrides = ( (ComponentAuditingData) this.auditedPropertiesHolder )
					.getAuditingOverrides();
			for ( org.hibernate.envers.AuditOverride override : overrides ) {
				if ( property.getName().equals( override.name() ) ) {
					// the override applies to this property
					if ( !override.isAudited() ) {
						return false;
					}
					else {
						if ( override.auditJoinTable() != null ) {
							propertyAuditingData.setJoinTable( override.auditJoinTable() );
						}
					}
				}
			}

		}
		return true;
	}

	private void readPersistentPropertiesAccess() {
		List<org.hibernate.envers.configuration.metadata.xml.jaxb.PropertyType> propertyL = clazz.getProperty();
		for ( org.hibernate.envers.configuration.metadata.xml.jaxb.PropertyType property : propertyL ) {
			if ( "field".equals( property.getPropertyAccessorName() ) ) {
				fieldAccessedPersistentProperties.add( property.getName() );
			}
			else {
				propertyAccessedPersistentProperties.add( property.getName() );
			}
		}
	}

	/**
	 * Just work on the defined Audit-Parents of the @Audited annotation
	 * 
	 * @param clazz
	 * @param declaredAuditedSuperclasses
	 */
	private void doGetDeclaredAuditedSuperClasses(ClazzType clazz, Set<ClazzType> declaredAuditedSuperclasses) {
		// TODO: JUST PARENTS FROM THE @Audited annotation ?? Is that right ???
		if ( clazz.getAudited() != null && clazz.getAudited().getAuditParent() != null
				&& clazz.getAudited().getAuditParent().size() > 0 ) {
			for ( AuditParent auditParent : clazz.getAudited().getAuditParent() ) {
				ClazzType parentClazz = XmlConfigurationTools.getClazzForName( auditParent.getClassName(),
						em.getEntity() );
				// TODO: CHECK ON ASSIGNABILITY TO BE SURE THAT EVERYTHIN IS CONFIGURED RIGHT
				declaredAuditedSuperclasses.add( clazz );
			}
		}
		if ( clazz.getParent() != null ) {
			doGetDeclaredAuditedSuperClasses(
					XmlConfigurationTools.getClazzForName( clazz.getParent(), em.getEntity() ),
					declaredAuditedSuperclasses );
		}

	}

	private static AuditJoinTable DEFAULT_AUDIT_JOIN_TABLE = new AuditJoinTable() {
		public String name() {
			return "";
		}

		public String schema() {
			return "";
		}

		public String catalog() {
			return "";
		}

		public JoinColumn[] inverseJoinColumns() {
			return new JoinColumn[0];
		}

		public Class<? extends Annotation> annotationType() {
			return this.getClass();
		}
	};

}
