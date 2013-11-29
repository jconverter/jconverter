package org.jconverter.converter.catalog.number;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;

public class NumberToBooleanConverter extends DefaultConverter<Number, Boolean> {

	@Override
	public Boolean apply(Number source, Type targetType, JConverter context) {
		return source.longValue() != 0;
	}

}
