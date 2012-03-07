package org.hibernate.envers.configuration.metadata.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hibernate.MappingException;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditJoinTable;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditOverride;
import org.hibernate.envers.configuration.metadata.xml.jaxb.AuditOverrides;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.EnversMappingType;
import org.hibernate.envers.configuration.metadata.xml.jaxb.JoinColumn;
import org.hibernate.mapping.PersistentClass;

/**
 * Tools for handling the Xml configuration
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlConfigurationTools {

	/**
	 * Reads the Envers-Xml-Mapping Xml File with Jaxb into Objects.
	 * 
	 * @param filePath
	 * @return <code>null</code> if an error occured or the EnversMapping if everything is OK.
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static EnversMappingType readEnversMapping(String filePath) {
		JAXBContext jc;
		Unmarshaller um;
		try {
			jc = JAXBContext.newInstance( "org.hibernate.envers.configuration.metadata.xml.jaxb" );
			um = jc.createUnmarshaller();
			JAXBElement jaxbElement = (JAXBElement) um.unmarshal( new FileInputStream( new File( filePath ) ) );
			EnversMappingType em = (EnversMappingType) jaxbElement.getValue();
			return em;
		}
		catch ( JAXBException e ) {
			throw new MappingException( "Error while Reading the Envers-Xml-Configuration File: '" + filePath + "'", e );
		}
		catch ( FileNotFoundException e ) {
			throw new MappingException( "Error while Reading the Envers-Xml-Configuration File: '" + filePath + "'", e );
		}
	}

	/**
	 * Gets the specific configured RevisionEnity if there is one.
	 * 
	 * @param em
	 *            the EnversXmlConfiguration
	 * @return the revision Entity or <code>null</code> if no RevisionEntity was specified in the EnversXmlConfiguration
	 */
	public static ClazzType getUniqueRevisionEntity(EnversMappingType em) {
		ClazzType revisionEntity = null;
		List<ClazzType> clazzL = em.getEntity();
		for ( ClazzType clazz : clazzL ) {
			if ( clazz.getRevisionEntity() != null ) {
				if ( revisionEntity != null )
					throw new MappingException( "Only one entity may be configured as RevisionEntity!" );
				revisionEntity = clazz;
			}
		}
		return revisionEntity;
	}

	/**
	 * Checks if the PersistentClass is the RevisionEntity or not.
	 * 
	 * @param pc
	 * @param em
	 * @return
	 */
	public static boolean isRevisionEntity(PersistentClass pc, EnversMappingType em) {
		ClazzType revisionEntity = XmlConfigurationTools.getUniqueRevisionEntity( em );
		if ( pc.getClassName().equals( revisionEntity.getName() ) ) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Searches for the Name of the Clazz in the List and returns the Clazz when found.
	 * 
	 * @param name
	 * @param clazzList
	 * @return
	 */
	public static ClazzType getClazzForName(String name, List<ClazzType> clazzList) {
		for ( ClazzType clazz : clazzList ) {
			if ( clazz.getName().equals( name ) )
				return clazz;
		}
		throw new MappingException( "The Class '" + name
				+ "' configured as an AuditParent in the Audited Annotation could not be found !" );

	}

	/**
	 * Transfers the Object which represents an Xml-AuditOverrides-Element into an XmlAuditOverrides Element which
	 * implements @AuditOverrides annotation.
	 * 
	 * @param auditOverride
	 * @return
	 */
	public static XmlAuditOverrides transferToXmlAudtOverrides(AuditOverrides auditOverrides) {
		XmlAuditOverrides xmlAuditOverrides = new XmlAuditOverrides();
		if ( auditOverrides.getAuditOverride() != null ) {
			XmlAuditOverride xmlAuditOverrideA[] = new XmlAuditOverride[auditOverrides.getAuditOverride().size()];
			int counter = 0;
			for ( AuditOverride auditOverride : auditOverrides.getAuditOverride() ) {
				xmlAuditOverrideA[counter++] = XmlConfigurationTools.transferToXmlAudtOverride( auditOverride );
			}
			xmlAuditOverrides.setAuditOverrides( xmlAuditOverrideA );
		}
		return xmlAuditOverrides;
	}

	/**
	 * Transfers the Object which represents an Xml-AuditOverride-Element into an XmlAuditOverride Element which
	 * implements @AuditOverride annotation.
	 * 
	 * @param auditOverride
	 * @return
	 */
	public static XmlAuditOverride transferToXmlAudtOverride(AuditOverride auditOverride) {
		XmlAuditOverride xmlAuditOverride = new XmlAuditOverride();
		xmlAuditOverride.setName( auditOverride.getName() );
		xmlAuditOverride.setAudited( auditOverride.isIsAudited() );
		if ( auditOverride.getAuditJoinTable() != null ) {
			xmlAuditOverride.setXmlAuditJoinTable( XmlConfigurationTools.transferToXmlAuditJoinTable( auditOverride
					.getAuditJoinTable() ) );
		}
		return xmlAuditOverride;
	}

	/**
	 * Transfers the Object which represents an Xml-AuditJoinTable into an XmlAuditJoinTable Element which implements
	 * the @AuditJoinTable annotation
	 * 
	 * @param auditJoinTable
	 * @return
	 */
	public static XmlAuditJoinTable transferToXmlAuditJoinTable(AuditJoinTable auditJoinTable) {
		XmlAuditJoinTable xmlAuditJoinTable = new XmlAuditJoinTable();
		xmlAuditJoinTable.setCatalog( auditJoinTable.getCatalog() );
		xmlAuditJoinTable.setName( auditJoinTable.getName() );
		xmlAuditJoinTable.setSchema( auditJoinTable.getSchema() );
		List<JoinColumn> joinColumnList = auditJoinTable.getJoinColumn();
		XmlJoinColumn xmlJoinColumnA[] = new XmlJoinColumn[joinColumnList.size()];
		int counter = 0;
		for ( JoinColumn joinColumn : joinColumnList ) {
			xmlJoinColumnA[counter++] = XmlConfigurationTools.transferToXmlInverseColumn( joinColumn );
		}
		xmlAuditJoinTable.setInverseJoinColumns( xmlJoinColumnA );
		return xmlAuditJoinTable;

	}

	/**
	 * Transfers the Object which represents an Xml-JoinColumn into an XmlJoinColumn Element which implements the @JoinColumn
	 * annotation
	 * 
	 * @param auditJoinTable
	 * @return
	 */
	public static XmlJoinColumn transferToXmlInverseColumn(JoinColumn joinColumn) {
		XmlJoinColumn xmlJoinColumn = new XmlJoinColumn();
		xmlJoinColumn.setColumnDefinition( joinColumn.getColumnDefinition() );
		xmlJoinColumn.setInsertable( joinColumn.isInsertable() );
		xmlJoinColumn.setName( joinColumn.getName() );
		xmlJoinColumn.setNullable( joinColumn.isNullable() );
		xmlJoinColumn.setReferencedColumnsName( joinColumn.getReferencedColumnName() );
		xmlJoinColumn.setTable( joinColumn.getTable() );
		xmlJoinColumn.setUnique( joinColumn.isUnique() );
		xmlJoinColumn.setUpdateable( joinColumn.isUpdatable() );
		return xmlJoinColumn;
	}

}
