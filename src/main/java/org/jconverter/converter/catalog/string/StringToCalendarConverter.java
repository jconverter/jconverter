package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class StringToCalendarConverter implements Converter<String, Calendar> {

	@Override
	public Calendar apply(String source, Type targetType, JConverter context) {
		Calendar cal = context.instantiate(Calendar.class);
		Date date = new StringToDateConverter().apply(source, Date.class, context);
		cal.setTime(date);
		return cal;
	}

}
