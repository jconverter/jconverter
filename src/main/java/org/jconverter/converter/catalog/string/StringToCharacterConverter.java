package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class StringToCharacterConverter implements Converter<String, Character> {

	@Override
	public Character apply(String source, Type targetType, JConverter context) {
		return source.charAt(0);
	}

}
