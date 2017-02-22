package org.jconverter.converter.catalog.string;

import static org.jconverter.converter.ConversionGoal.conversionGoal;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.DelegateConversionException;
import org.jconverter.converter.TypeDomain;

public class StringToCharacterConverter implements Converter<String, Character> {

	@Override
	public Character apply(String source, TypeDomain target, JConverter context) {
		if (source.length() > 1) {
			throw new DelegateConversionException(conversionGoal(source, target));
		} else {
			return source.charAt(0);
		}
	}

}
