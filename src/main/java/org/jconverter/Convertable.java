package org.jconverter;

import java.lang.reflect.Type;

/**
 * A class wrapping an object that can be converted to other types.
 * @author sergioc
 *
 */
public class Convertable {

	private final Object source;
	private final JConverter context;
	
	public Convertable(Object source, JConverter context) {
		this.source = source;
		this.context = context;
	}
	
	public <T> T as(Type targetType) {
		return context.convert(source, targetType);
	}
	
}
