package org.jconverter.converter.catalog.string;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.util.Calendar;
import java.util.Date;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class StringToCalendarConverter implements Converter<String, Calendar> {

	@Override
	public Calendar apply(String source, TypeDomain target, JConverter context) {
		Calendar cal = context.instantiate(Calendar.class);
		Date date = new StringToDateConverter().apply(source, typeDomain(Date.class), context);
		cal.setTime(date);
		return cal;
	}

}
