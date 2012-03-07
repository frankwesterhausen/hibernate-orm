package org.hibernate.envers.configuration.metadata.xml;

import java.lang.annotation.Annotation;
import javax.persistence.JoinColumn;

/**
 * Class to implement the Annotations on which Envers is based normaly.
 * 
 * @author Frank Westerhausen
 * 
 */
public class XmlJoinColumn implements JoinColumn {

	private String columnDefinition;
	private Boolean insertable;
	private String name;
	private Boolean nullable;
	private String referencedColumnsName;
	private String table;
	private Boolean unique;
	private Boolean updateable;

	public Class<? extends Annotation> annotationType() {
		return JoinColumn.class;
	}

	public String columnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	public boolean insertable() {
		return insertable;
	}

	public void setInsertable(Boolean insertable) {
		this.insertable = insertable;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean nullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String referencedColumnName() {
		return referencedColumnsName;
	}

	public void setReferencedColumnsName(String referencedColumnsName) {
		this.referencedColumnsName = referencedColumnsName;
	}

	public String table() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public boolean unique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public boolean updatable() {
		return updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

}
