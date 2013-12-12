package org.jconverter.converter;

import java.lang.reflect.Type;

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
	 * @param targetType the target type.
	 * @param context the context.
	 * @return the source type converted to the target type according to the given context.
	 */
	public V apply(T source, Type targetType, JConverter context);
	
}
