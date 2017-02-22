package org.typetools.reification;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Disclaimer: Adapted from the Guava library equivalent (internal) class.
 * @author sergioc
 *
 */
public class WildcardTypeImpl implements WildcardType, Serializable {

	private static final long serialVersionUID = 0;

	//WildcardType instance can have only one bound (either lower or upper).
	//Source: http://docs.oracle.com/javase/tutorial/java/generics/lowerBounded.html
	private final ImmutableList<Type> lowerBounds;
	private final ImmutableList<Type> upperBounds;

	public WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
		this.lowerBounds = ImmutableList.copyOf(lowerBounds);
		this.upperBounds = ImmutableList.copyOf(upperBounds);
	}

	@Override
	public Type[] getLowerBounds() {
		return lowerBounds.toArray(new Type[lowerBounds.size()]);
	}

	@Override
	public Type[] getUpperBounds() {
		return upperBounds.toArray(new Type[upperBounds.size()]);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WildcardType) {
			WildcardType that = (WildcardType) obj;
			return lowerBounds.equals(Arrays.asList(that.getLowerBounds()))
					&& upperBounds.equals(Arrays.asList(that.getUpperBounds()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return lowerBounds.hashCode() ^ upperBounds.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("?");
		for (Type lowerBound : lowerBounds) {
			builder.append(" super ").append(toString(lowerBound));
		}
		for (Type upperBound : filterUpperBounds(upperBounds)) {
			builder.append(" extends ").append(toString(upperBound));
		}
		return builder.toString();
	}

	private static Iterable<Type> filterUpperBounds(Iterable<Type> bounds) {
		return Iterables.filter(bounds,
				Predicates.not(Predicates.<Type> equalTo(Object.class)));
	}
	
	private static String toString(Type type) {
	    return (type instanceof Class)
	        ? ((Class<?>) type).getName()
	        : type.toString();
	  }

}
