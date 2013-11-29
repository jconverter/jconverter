package org.jconverter.converter.catalog;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.ConversionException;
import org.jconverter.converter.Converter.DefaultConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class ObjectToStringConverter extends DefaultConverter<Object, String> {

	@Override
	public String apply(Object source, Type targetType, JConverter context) {
		Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
		if(targetClass.equals(String.class)) {
			return source.toString();
		}
		throw new ConversionException();
	}
	
}
