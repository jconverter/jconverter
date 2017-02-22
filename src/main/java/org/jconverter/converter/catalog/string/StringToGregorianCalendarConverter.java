package org.jconverter.converter.catalog.string;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class StringToGregorianCalendarConverter implements Converter<String, GregorianCalendar> {

	@Override
	public GregorianCalendar apply(String source, TypeDomain target, JConverter context) {
		GregorianCalendar cal = new GregorianCalendar();
		Date date = new StringToDateConverter().apply(source, typeDomain(Date.class), context);
		cal.setTime(date);
		return cal;
	}

}
