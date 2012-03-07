package org.hibernate.envers.configuration.metadata.xml;

import java.util.Iterator;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.envers.configuration.metadata.reader.PersistentPropertiesSource;
import org.hibernate.envers.configuration.metadata.xml.jaxb.ClazzType;
import org.hibernate.mapping.Property;

/**
 * Class to implement the PersistentPropertiesSource-Interface for the Xml configuration..
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlPersistentClassPropertiesSource implements PersistentPropertiesSource {

	// TODO: CHECK IF THIS CAN BE DONE OR NOT ????

	private ClazzType clazz;

	public XmlPersistentClassPropertiesSource(ClazzType clazz) {
		this.clazz = clazz;
	}

	public Iterator<Property> getPropertyIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public Property getProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	public XClass getXClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
