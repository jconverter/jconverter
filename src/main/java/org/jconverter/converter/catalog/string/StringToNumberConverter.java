package org.jconverter.converter.catalog.string;

import java.text.NumberFormat;
import java.text.ParseException;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;

public class StringToNumberConverter<T extends Number> implements Converter<String, T> {

	@Override
	public T apply(String source, TypeDomain target, JConverter context) {
		Class<?> numberClass = target.getRawClass();
		Number number;
		try {
			NumberFormat nf = context.instantiate(NumberFormat.class);
			number = nf.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} 
		return (T) NumberToNumberConverter.numberToNumber(number, numberClass);
	}
}
