package org.jconverter.converter.catalog.number;

import java.util.Calendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class NumberToCalendarConverter implements Converter<Number, Calendar> {

	@Override
	public Calendar apply(Number source, TypeDomain target, JConverter context) {
		Calendar cal = context.instantiate(Calendar.class);
		cal.setTimeInMillis(source.longValue());
		return cal;
	}

}
