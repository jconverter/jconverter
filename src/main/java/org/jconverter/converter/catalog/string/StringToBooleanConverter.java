package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;

public class StringToBooleanConverter extends DefaultConverter<String, Boolean> {

	@Override
	public Boolean apply(String source, Type targetType, JConverter context) {
		return Boolean.parseBoolean(source);
	}

}
