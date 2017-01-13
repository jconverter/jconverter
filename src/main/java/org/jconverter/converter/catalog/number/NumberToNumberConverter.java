package org.jconverter.converter.catalog.number;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.util.typewrapper.TypeWrapper;

public class NumberToNumberConverter<T extends Number> implements Converter<Number, T> {

	@Override
	public T apply(Number source, Type targetType, JConverter context) {
		Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
		return (T) numberToNumber(source, (Class)targetType);
	}
	
	public static Number numberToNumber(Number number, Class numberClass) {
		if(Long.class.equals(numberClass))
			return number.longValue();
		else if(Integer.class.equals(numberClass))
			return number.intValue();
		else if(Short.class.equals(numberClass))
			return number.shortValue();
		else if(Byte.class.equals(numberClass))
			return new Byte(number.byteValue());
		else if(Double.class.equals(numberClass))
			return number.doubleValue();
		else if(Float.class.equals(numberClass))
			return number.floatValue();
		else if(BigInteger.class.equals(numberClass))
			return new BigInteger(new Long(number.longValue()).toString());
		else if(BigDecimal.class.equals(numberClass))
			return new BigDecimal(number.doubleValue());
		else if(AtomicInteger.class.equals(numberClass))
			return new AtomicInteger(number.intValue());
		else if(AtomicLong.class.equals(numberClass))
			return new AtomicLong(number.longValue());
		else
			throw new RuntimeException("Not a number type.");
	}
	
}
