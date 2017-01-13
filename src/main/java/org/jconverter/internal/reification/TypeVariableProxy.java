package org.jconverter.internal.reification;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVariableProxy<D extends GenericDeclaration> implements TypeVariable<D> {

	private final TypeVariable<D> typeVariable;

	public TypeVariableProxy(TypeVariable<D> typeVariable) {
		this.typeVariable = typeVariable;
	}
	
	public Type[] getBounds() {
		return typeVariable.getBounds();
	}

	public D getGenericDeclaration() {
		return typeVariable.getGenericDeclaration();
	}

	public String getName() {
		return typeVariable.getName();
	}

	public AnnotatedType[] getAnnotatedBounds() {
		return typeVariable.getAnnotatedBounds();
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return typeVariable.getAnnotation(annotationClass);
	}

	public Annotation[] getAnnotations() {
		return typeVariable.getAnnotations();
	}

	public Annotation[] getDeclaredAnnotations() {
		return typeVariable.getDeclaredAnnotations();
	}
	
}
