package org.jconverter.converter;

import java.lang.reflect.Type;

import com.google.common.base.Function;


public interface ConverterManager {
	
	public void register(Object converterKey, Converter converter);
	
	public Type convert(Object converterKey, Object object, Type targetType);

}
