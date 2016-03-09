package io.allset.testzen.entity;

import io.allset.testzen.type.Type;

/**
 * 
 * @author Ram Lakshmanan
 */
public class Field {

	private String name;
	private Type type;
	private Object value;
	private Object positiveValue;
	
	public static final String EMPTY_VALUE = "";
	
	public static final Field EMPTY_FIELD = new Field("", null, EMPTY_VALUE);
	
	public Field() {
	}
	
	public Field(String fieldName, Type type) {

		super();
		this.name = fieldName;
		this.type = type;
	}

	public Field(String name, Type type, Object value) {

		this(name, type);
		this.value = value;
	}

	public Field(String name, Type type, Object value, Object positiveValue) {

		this(name, type, value);
		this.positiveValue = positiveValue;
	}
	

	public Object cloneWithOutValue() throws CloneNotSupportedException {
		
		Field newField = new Field();
		newField.name = this.name;
		newField.type = this.type;
		newField.value = null;
		newField.positiveValue = this.positiveValue;		
		
		return newField;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		
		Field newField = new Field();
		newField.name = this.name;
		newField.type = this.type;
		newField.value = this.value;
		newField.positiveValue = this.positiveValue;		
		
		return newField;
	}
	
	@Override
	public String toString() {
		
		//return name + " - " + type + " - " + value;
		return value != null ? value.toString() : "";
	}
	
	/*@Override
	public boolean equals(Object anotherObject) {
		
		if (anotherObject == null) {
			return Boolean.FALSE;
		}
		
		Field anotherField = (Field)anotherObject;
		
		return anotherField.getName().equals(this.name)
				&& anotherField.getValue().equals(this.value)
				&& anotherField.getType().equals(this.type);
	}*/
	
	
	public String getName() {
	
		return name;
	}
	
	public void setName(String name) {
	
		this.name = name;
	}
	
	public Type getType() {
	
		return type;
	}
	
	public void setType(Type type) {
	
		this.type = type;
	}
	
	public Object getValue() {
	
		return value;
	}
	
	public void setValue(Object value) {
	
		this.value = value;
	}

	public Object getPositiveValue() {

		// TODO Auto-generated method stub
		return positiveValue;
	}
}
