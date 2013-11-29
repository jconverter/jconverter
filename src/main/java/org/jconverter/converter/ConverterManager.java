package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;


public interface ConverterManager {

	public static class ConverterKey extends Key {
		public ConverterKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new ConverterKey(JConverter.DEFAULT_JCONVERTER_KEY);
	
	public abstract void register(Object converterKey, Converter converter);

	public abstract <T> T convert(Object converterKey, Object object, Type targetType, JConverter context);

}
