package org.jconverter.converter.catalog.object;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.ConversionException;
import org.jconverter.converter.Converter;
import org.jconverter.internal.reflection.typewrapper.TypeWrapper;

public class ObjectToStringConverter implements Converter<Object, String> {

	@Override
	public String apply(Object source, Type targetType, JConverter context) {
		Class<?> targetClass = TypeWrapper.wrap(targetType).getRawClass();
		if(targetClass.equals(String.class)) {
			return source.toString();
		}
		throw new ConversionException();
	}
	
}
