package org.jconverter.converter.catalog.number;

import java.lang.reflect.Type;
import java.util.Calendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class NumberToCalendarConverter implements Converter<Number, Calendar> {

	@Override
	public Calendar apply(Number source, Type targetType, JConverter context) {
		Calendar cal = context.instantiate(Calendar.class);
		cal.setTimeInMillis(source.longValue());
		return cal;
	}

}
