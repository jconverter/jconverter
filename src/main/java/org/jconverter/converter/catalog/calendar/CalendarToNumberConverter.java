package org.jconverter.converter.catalog.calendar;

import java.lang.reflect.Type;
import java.util.Calendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;

public class CalendarToNumberConverter implements Converter<Calendar, Number> {

	@Override
	public Number apply(Calendar source, Type targetType, JConverter context) {
		Long timeInMillis = source.getTimeInMillis();
		return NumberToNumberConverter.numberToNumber(timeInMillis, (Class<?>) targetType);
	}

}
