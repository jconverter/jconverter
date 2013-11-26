package org.jconverter.converter.catalog;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jconverter.JConverter;
import org.jconverter.converter.ConversionException;
import org.jconverter.converter.Converter.DefaultConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class StringConverter extends DefaultConverter<String, Object> {

	@Override
	public Object apply(String source, Type targetType, JConverter context) {
		Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
		if(Number.class.isAssignableFrom(targetClass)) {
			return stringToNumber(source, targetType);
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
	
	private static Number stringToNumber(String string, Type type) {
		Number number;
		try {
			number = NumberFormat.getInstance().parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} //"a general-purpose number format for the current default locale."
		if(Long.class.equals(type))
			return new Long(number.longValue());
		else if(Integer.class.equals(type))
			return new Integer(number.intValue());
		else if(Short.class.equals(type))
			return new Short(number.shortValue());
		else if(Byte.class.equals(type))
			return new Byte(number.byteValue());
		else if(Double.class.equals(type))
			return new Double(number.doubleValue());
		else if(Float.class.equals(type))
			return new Float(number.floatValue());
		else if(BigInteger.class.equals(type))
			return new BigInteger(new Long(number.longValue()).toString());
		else if(BigDecimal.class.equals(type))
			return new BigDecimal(number.doubleValue());
		else if(AtomicInteger.class.equals(type))
			return new AtomicInteger(number.intValue());
		else if(AtomicLong.class.equals(type))
			return new AtomicLong(number.longValue());
		else
			throw new RuntimeException("Not a number type.");
	}
	

}
