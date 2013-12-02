package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;


public abstract class ConverterManager {

	public static class ConverterKey extends Key {
		public ConverterKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new ConverterKey(JConverter.DEFAULT_JCONVERTER_KEY);
	
	public void register(Converter converter) {
		register(DEFAULT_KEY, converter);
	}
	
	public abstract void register(Object converterKey, Converter converter);

	public <T> T convert(Object object, Type targetType, JConverter context) {
		return convert(DEFAULT_KEY, object, targetType, context);
	}
	
	public abstract <T> T convert(Object converterKey, Object object, Type targetType, JConverter context);

}
