package org.jconverter.converter.catalog.calendar;

import java.util.Calendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;

public class CalendarToNumberConverter implements Converter<Calendar, Number> {

	@Override
	public Number apply(Calendar source, TypeDomain target, JConverter context) {
		Long timeInMillis = source.getTimeInMillis();
		return NumberToNumberConverter.numberToNumber(timeInMillis, target.getRawClass());
	}

}
