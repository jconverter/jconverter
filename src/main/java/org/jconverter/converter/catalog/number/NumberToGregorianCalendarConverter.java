package org.jconverter.converter.catalog.number;

import java.lang.reflect.Type;
import java.util.GregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class NumberToGregorianCalendarConverter implements Converter<Number, GregorianCalendar> {

	@Override
	public GregorianCalendar apply(Number source, Type targetType, JConverter context) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(source.longValue());
		return cal;
	}

}
