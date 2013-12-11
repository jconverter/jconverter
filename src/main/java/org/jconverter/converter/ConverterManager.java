package org.jconverter.converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.catalog.ObjectToStringConverter;
import org.jconverter.converter.catalog.array.ArrayToArrayConverter;
import org.jconverter.converter.catalog.array.ArrayToCollectionConverter;
import org.jconverter.converter.catalog.array.ArrayToEnumerationConverter;
import org.jconverter.converter.catalog.array.ArrayToIterableConverter;
import org.jconverter.converter.catalog.array.ArrayToIteratorConverter;
import org.jconverter.converter.catalog.array.ArrayToMapConverter;
import org.jconverter.converter.catalog.calendar.CalendarToNumberConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToArrayConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToCollectionConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToEnumerationConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToIterableConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToIteratorConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToMapConverter;
import org.jconverter.converter.catalog.iterable.IterableToArrayConverter;
import org.jconverter.converter.catalog.iterable.IterableToCollectionConverter;
import org.jconverter.converter.catalog.iterable.IterableToEnumerationConverter;
import org.jconverter.converter.catalog.iterable.IterableToIterableConverter;
import org.jconverter.converter.catalog.iterable.IterableToIteratorConverter;
import org.jconverter.converter.catalog.iterable.IterableToMapConverter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;
import org.jconverter.converter.catalog.map.MapToArrayConverter;
import org.jconverter.converter.catalog.map.MapToCollectionConverter;
import org.jconverter.converter.catalog.map.MapToEnumerationConverter;
import org.jconverter.converter.catalog.map.MapToIterableConverter;
import org.jconverter.converter.catalog.map.MapToIteratorConverter;
import org.jconverter.converter.catalog.map.MapToMapConverter;
import org.jconverter.converter.catalog.number.NumberToBooleanConverter;
import org.jconverter.converter.catalog.number.NumberToCalendarConverter;
import org.jconverter.converter.catalog.number.NumberToGregorianCalendarConverter;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;
import org.jconverter.converter.catalog.number.NumberToXMLGregorianCalendarConverter;
import org.jconverter.converter.catalog.string.StringToBooleanConverter;
import org.jconverter.converter.catalog.string.StringToCalendarConverter;
import org.jconverter.converter.catalog.string.StringToCharacterConverter;
import org.jconverter.converter.catalog.string.StringToDateConverter;
import org.jconverter.converter.catalog.string.StringToGregorianCalendarConverter;
import org.jconverter.converter.catalog.string.StringToNumberConverter;
import org.jconverter.converter.catalog.string.StringToXMLGregorianCalendarConverter;
import org.jgum.strategy.ChainOfResponsibility;


public abstract class ConverterManager {
	
	public static final Object DEFAULT_KEY = new Object();
	
	//TODO move to the Converter interface when Java8 is out
	public static <T,V> ChainOfResponsibility<Converter<T,V>, V> chainConverters(List<Converter<T,V>> converters) {
		List<Converter<T,V>> typedConverters = new ArrayList<>();
		for(Converter<T,V> converter : converters)
			typedConverters.add(TypedConverter.forConverter(converter));
		ChainOfResponsibility<Converter<T,V>,V> chain = new ChainOfResponsibility<>(typedConverters);
		return chain;
	}
	
	/**
	 * Registers default converters in the given converter manager.
	 * @param converterManager a converter manager.
	 */
	public static void registerDefaults(ConverterManager converterManager) {
		converterManager.register(new CalendarToNumberConverter());
		
		converterManager.register(new NumberToNumberConverter());
		converterManager.register(new NumberToBooleanConverter());
		converterManager.register(new NumberToXMLGregorianCalendarConverter());
		converterManager.register(new NumberToGregorianCalendarConverter());
		converterManager.register(new NumberToCalendarConverter());
		
		converterManager.register(new StringToNumberConverter());
		converterManager.register(new StringToBooleanConverter());
		converterManager.register(new StringToCharacterConverter());
		converterManager.register(new StringToDateConverter());
		converterManager.register(new StringToXMLGregorianCalendarConverter());
		converterManager.register(new StringToGregorianCalendarConverter());
		converterManager.register(new StringToCalendarConverter());
		
		converterManager.register(new IteratorToArrayConverter());
		converterManager.register(new IteratorToCollectionConverter());
		converterManager.register(new IteratorToEnumerationConverter());
		converterManager.register(new IteratorToIterableConverter());
		converterManager.register(new IteratorToIteratorConverter());
		converterManager.register(new IteratorToMapConverter());
		
		converterManager.register(new IterableToArrayConverter());
		converterManager.register(new IterableToCollectionConverter());
		converterManager.register(new IterableToEnumerationConverter());
		converterManager.register(new IterableToIterableConverter());
		converterManager.register(new IterableToIteratorConverter());
		converterManager.register(new IterableToMapConverter());
		
		converterManager.register(new EnumerationToArrayConverter());
		converterManager.register(new EnumerationToCollectionConverter());
		converterManager.register(new EnumerationToEnumerationConverter());
		converterManager.register(new EnumerationToIterableConverter());
		converterManager.register(new EnumerationToIteratorConverter());
		converterManager.register(new EnumerationToMapConverter());
		
		converterManager.register(new ArrayToArrayConverter());
		converterManager.register(new ArrayToCollectionConverter());
		converterManager.register(new ArrayToEnumerationConverter());
		converterManager.register(new ArrayToIterableConverter());
		converterManager.register(new ArrayToIteratorConverter());
		converterManager.register(new ArrayToMapConverter());
		
		converterManager.register(new MapToArrayConverter());
		converterManager.register(new MapToCollectionConverter());
		converterManager.register(new MapToEnumerationConverter());
		converterManager.register(new MapToIterableConverter());
		converterManager.register(new MapToIteratorConverter());
		converterManager.register(new MapToMapConverter());
		
		converterManager.register(new ObjectToStringConverter());
	}
	
	public void register(Converter converter) {
		register(DEFAULT_KEY, converter);
	}
	
	public abstract void register(Object converterKey, Converter converter);

	public <T> T convert(Object object, Type targetType, JConverter context) {
		return convert(DEFAULT_KEY, object, targetType, context);
	}
	
	public abstract <T> T convert(Object converterKey, Object object, Type targetType, JConverter context);

}
