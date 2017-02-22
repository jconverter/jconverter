package org.jconverter.converter.catalog.number;

import java.util.GregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class NumberToGregorianCalendarConverter implements Converter<Number, GregorianCalendar> {

	@Override
	public GregorianCalendar apply(Number source, TypeDomain target, JConverter context) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(source.longValue());
		return cal;
	}

}
