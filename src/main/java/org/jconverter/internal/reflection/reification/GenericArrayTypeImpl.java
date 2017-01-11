package org.jconverter.internal.reflection.reification;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Disclaimer: toString, equals and hashCode methods are adapted from
 * sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl
 * (this implementation was not used since it is internal to a VM implementation)
 * 
 * @author scastro
 * 
 */
public class GenericArrayTypeImpl implements GenericArrayType {

	private final Type genericComponentType;

	public GenericArrayTypeImpl(Type genericComponentType) {
		this.genericComponentType = genericComponentType;
	}

	public Type getGenericComponentType() {
		return genericComponentType;
	}

	@Override
	public String toString() {
		Type componentType = getGenericComponentType();
		StringBuilder sb = new StringBuilder();
		if (componentType instanceof Class)
			sb.append(((Class) componentType).getName());
		else
			sb.append(componentType.toString());
		sb.append("[]");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GenericArrayType) {
			GenericArrayType param = (GenericArrayType) o;
			return getGenericComponentType().equals(
					param.getGenericComponentType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (genericComponentType == null) ? 0 : genericComponentType
				.hashCode();
	}
}
