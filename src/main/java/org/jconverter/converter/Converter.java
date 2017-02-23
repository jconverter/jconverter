package org.jconverter.converter;

import org.jconverter.JConverter;

/**
 * Interface implemented by all the JConverter converters.
 * @author sergioc
 *
 * @param <T> the source type.
 * @param <V> the target type.
 */
public interface Converter<T,V> {
	
	/**
	 * 
	 * @param source the object to convert.
	 * @param target the target domain.
	 * @param context the context.
	 * @return the source type converted to the target domain according to the given context.
	 */
	V apply(T source, TypeDomain target, JConverter context);

}
