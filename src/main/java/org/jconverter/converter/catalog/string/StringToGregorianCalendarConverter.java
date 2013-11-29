package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class StringToGregorianCalendarConverter implements Converter<String, GregorianCalendar> {

	@Override
	public GregorianCalendar apply(String source, Type targetType, JConverter context) {
		GregorianCalendar cal = new GregorianCalendar();
		Date date = new StringToDateConverter().apply(source, Date.class, context);
		cal.setTime(date);
		return cal;
	}

}
