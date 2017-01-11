package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;
import org.jconverter.internal.reflection.typewrapper.TypeWrapper;

public class StringToNumberConverter<T extends Number> implements Converter<String, T> {

	@Override
	public T apply(String source, Type targetType, JConverter context) {
		Class<?> numberClass = TypeWrapper.wrap(targetType).getRawClass();
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
