package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;

public class StringToCharacterConverter extends DefaultConverter<String, Character> {

	@Override
	public Character apply(String source, Type targetType, JConverter context) {
		return source.charAt(0);
	}

}
