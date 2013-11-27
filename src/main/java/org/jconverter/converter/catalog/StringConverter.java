package org.jconverter.converter.catalog;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jconverter.JConverter;
import org.jconverter.converter.ConversionException;
import org.jconverter.converter.Converter.DefaultConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class StringConverter extends DefaultConverter<String, Object> {

	@Override
	public Object apply(String source, Type targetType, JConverter context) {
		Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
		if(Number.class.isAssignableFrom(targetClass)) {
			return stringToNumber(source, (Class)targetType);
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
			Calendar cal = context.instantiate(Calendar.class);
			DateFormat df = new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			try {
				cal.setTime(df.parse(source));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			return cal;
		}
		throw new ConversionException();
	}
	
	private static Number stringToNumber(String string, Class numberClass) {
		Number number;
		try {
			number = NumberFormat.getInstance().parse(string); //"a general-purpose number format for the current default locale."
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} 
		return NumberConverter.numberToNumber(number, numberClass);
	}
	
}
