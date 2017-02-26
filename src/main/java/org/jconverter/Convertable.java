package org.jconverter;

import static org.jconverter.JConverter.jConverter;

import java.lang.reflect.Type;

/**
 * A (syntactic-sugar) class wrapping an object that can be converted to other types.
 * @author sergioc
 *
 */
public class Convertable {

	private final Object source;
	private final JConverter context;
	
	/**
	 * 
	 * @param source the object to convert.
	 */
	public Convertable(Object source) {
		this(source, jConverter());
	}
	
	/**
	 * 
	 * @param source the object to convert.
	 * @param context the conversion context.
	 */
	public Convertable(Object source, JConverter context) {
		this.source = source;
		this.context = context;
	}
	
	/**
	 * 
	 * @param targetType
	 * @return the wrapped object converted to the desired type.
	 */
	public <T> T as(Type targetType) {
		return context.convert(source, targetType);
	}
	
}
