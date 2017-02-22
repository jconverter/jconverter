package org.typetools;

import java.lang.reflect.Type;

public class IncompatibleTypesException extends RuntimeException {
	private Type type1;
	private Type type2;
	
	public IncompatibleTypesException(Type type1, Type type2) {
		this.type1 = type1;
		this.type2 = type2;
	}
	
	@Override 
	public String getMessage() {
		return "The types: " + type1 + " and " + type2 + " are not compatible for the requested operation.";
	}
}