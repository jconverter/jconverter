package org.jconverter.converter.catalog.object;

import static org.jconverter.converter.ConversionGoal.conversionGoal;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.DelegateConversionException;
import org.jconverter.converter.TypeDomain;

public class ObjectToStringConverter implements Converter<Object, String> {

	@Override
	public String apply(Object source, TypeDomain target, JConverter context) {
		if (target.getRawClass().equals(String.class)) {
			return source.toString();
		} else {
			throw new DelegateConversionException(conversionGoal(source, target));
		}
	}
	
}
