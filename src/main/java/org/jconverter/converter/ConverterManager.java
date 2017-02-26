package org.jconverter.converter;

import org.jcategory.category.Key;
import org.jconverter.JConverter;
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
import org.jconverter.converter.catalog.object.ObjectToStringConverter;
import org.jconverter.converter.catalog.string.StringToBooleanConverter;
import org.jconverter.converter.catalog.string.StringToCalendarConverter;
import org.jconverter.converter.catalog.string.StringToCharacterConverter;
import org.jconverter.converter.catalog.string.StringToDateConverter;
import org.jconverter.converter.catalog.string.StringToGregorianCalendarConverter;
import org.jconverter.converter.catalog.string.StringToNumberConverter;
import org.jconverter.converter.catalog.string.StringToXMLGregorianCalendarConverter;


public abstract class ConverterManager {

	/**
	 * Registers default converters in the given converter manager.
	 * @param converterManager a converter manager.
	 * @return the converter manager.
	 */
	public static ConverterManager registerDefaults(ConverterManager converterManager) {
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
		
		return converterManager;
	}

	public void register(Converter<?,?> converter) {
		register(ConverterKey.DEFAULT_KEY, converter);
	}

	public abstract void register(Key converterKey, Converter<?,?> converter);
	
	public abstract <T> T convert(Key converterKey, Object object, TypeDomain target, JConverter context);

}
