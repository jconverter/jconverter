package org.jconverter.converter.catalog.number;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class NumberToBooleanConverter implements Converter<Number, Boolean> {

	@Override
	public Boolean apply(Number source, TypeDomain target, JConverter context) {
		return source.longValue() != 0;
	}

}
