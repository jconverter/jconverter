package org.jconverter.internal.reflection.reification;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.common.collect.ImmutableList;

/**
 * Disclaimer: Adapted from the Guava library equivalent (internal) class.
 * @author sergioc
 *
 * @param <D>
 */
public class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D> {

	private final D genericDeclaration;
	private final String name;
	//Type parameters can have several upper bounds, but no lower bound.
	//Source: http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeParameters.html#FAQ107
	private final ImmutableList<Type> upperBounds;

	public TypeVariableImpl(D genericDeclaration, String name, Type[] upperBounds) {
		if(true)
			throw new RuntimeException("The " + TypeVariableImpl.class + " class should be compatible with Java 8.");
		this.genericDeclaration = checkNotNull(genericDeclaration);
		this.name = checkNotNull(name);
		this.upperBounds = ImmutableList.copyOf(upperBounds);
	}

	@Override
	public Type[] getBounds() {
		return upperBounds.toArray(new Type[upperBounds.size()]);
	}

	@Override
	public D getGenericDeclaration() {
		return genericDeclaration;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return genericDeclaration.hashCode() ^ name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeVariable) {
			TypeVariable<?> that = (TypeVariable<?>) obj;
			return name.equals(that.getName())
					&& genericDeclaration.equals(that.getGenericDeclaration());
		}
		return false;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return new Annotation[]{};
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return new Annotation[]{};
	}

	@Override
	public AnnotatedType[] getAnnotatedBounds() {
		return new AnnotatedType[]{};
	}

}
