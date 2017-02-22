package org.jconverter.converter.catalog.string;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class StringToBooleanConverter implements Converter<String, Boolean> {

	@Override
	public Boolean apply(String source, TypeDomain target, JConverter context) {
		return Boolean.parseBoolean(source);
	}

}
