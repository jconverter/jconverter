package org.jconverter.converter.catalog;

import java.lang.reflect.Type;
import java.util.Calendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;

public class CalendarConverter extends DefaultConverter<Calendar, Number> {

	@Override
	public Number apply(Calendar source, Type targetType, JConverter context) {
		Long timeInMillis = source.getTimeInMillis();
		return NumberConverter.numberToNumber(timeInMillis, (Class) targetType);
	}

}
