package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;

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
