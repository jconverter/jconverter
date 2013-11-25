package org.jconverter.typesolver;

import java.lang.reflect.Type;

/**
 * Assigns the best conversion target type to a source object.
 * @author sergioc
 *
 */
public interface TypeSolver<T> {
	
	public abstract Type getType(T object);
	
}
