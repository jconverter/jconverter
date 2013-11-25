package org.jconverter.catalog;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jconverter.converter.ConversionException;
import org.jconverter.converter.Converter.DefaultConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class StringConverter extends DefaultConverter<String, Object> {

	@Override
	public Object apply(String source, Type targetType) {
		Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
		if(Number.class.isAssignableFrom(targetClass)) {
			try {
				return NumberFormat.getInstance().parse(source); //"a general-purpose number format for the current default locale."
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else if(Boolean.class.equals(targetClass)){
			return Boolean.parseBoolean(source);
		} else if(Character.class.equals(targetClass)){
			return source.charAt(0);
		} else if(Date.class.equals(targetClass)) {
			DateFormat df = new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			try {
				return df.parse(source);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else if(Calendar.class.equals(targetClass)) {
			Calendar cal = Calendar.getInstance(); //"Gets a calendar using the default time zone and locale. The Calendar returned is based on the current time in the default time zone with the default locale."
			DateFormat df = new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			try {
				cal.setTime(df.parse(source));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			return cal;
		} else if(StringBuilder.class.equals(targetClass)) {
			return new StringBuilder(source);
		} else if(StringBuffer.class.equals(targetClass)) {
			return new StringBuffer(source);
		} else if(targetClass.isAssignableFrom(String.class))
			return source;
		throw new ConversionException();
	}

}
