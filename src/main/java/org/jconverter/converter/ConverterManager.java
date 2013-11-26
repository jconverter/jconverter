package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;


public abstract class ConverterManager {
	
	public static final Object DEFAULT_KEY = new Object();
	
	public void register(Converter converter) {
		register(DEFAULT_KEY, converter);
	}
	
	public abstract void register(Object converterKey, Converter converter);
	
	public <T> T convert(Object object, Type targetType, JConverter context) {
		return (T)convert(DEFAULT_KEY, object, targetType, context);
	}
	
	public abstract <T> T convert(Object converterKey, Object object, Type targetType, JConverter context);

}
