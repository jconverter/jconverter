package org.jconverter.factory;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jcategory.category.Key;

public abstract class FactoryManager {

	
	/**
	 * Registers default factories in the given factory manager.
	 * @param factoryManager an instantiation manager.
	 * @return the factory manager.
	 */
	public static FactoryManager registerDefaults(FactoryManager factoryManager) {
		factoryManager.register(ArrayDeque.class);
		factoryManager.register(HashMap.class);
		factoryManager.register(HashSet.class);
		factoryManager.register(ArrayList.class);
		factoryManager.register(new Factory<Calendar>() {
			@Override
			public Calendar instantiate(Type type) {
				return Calendar.getInstance(); //"Gets a calendar using the default time zone and locale. The Calendar returned is based on the current time in the default time zone with the default locale."
			}
		});
		factoryManager.register(new Factory<XMLGregorianCalendar>() {
			@Override
			public XMLGregorianCalendar instantiate(Type type) {
				try {
					return DatatypeFactory.newInstance().newXMLGregorianCalendar(); //All date/time datatype fields set to DatatypeConstants.FIELD_UNDEFINED or null.
				} catch (DatatypeConfigurationException e) {
					throw new RuntimeException(e);
				}
			}
		});
		factoryManager.register(new Factory<DateFormat>() {
			@Override
			public DateFormat instantiate(Type type) {
				return new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			}
		});
		factoryManager.register(new Factory<NumberFormat>() {
			@Override
			public NumberFormat instantiate(Type type) {
				return NumberFormat.getInstance(); //"a general-purpose number format for the current default locale."
			}
		});
		
		return factoryManager;
	}
	
	
	public void register(Class<?> clazz) {
		register(FactoryKey.DEFAULT_KEY, clazz);
	}
	
	public abstract void register(Key key, Class<?> clazz);
	
	public void register(List<Class<?>> classes, Factory<?> factory) {
		register(FactoryKey.DEFAULT_KEY, classes, factory);
	}
	
	public abstract void register(Key key, List<Class<?>> classes, Factory<?> factory);
	
	
	public void register(Factory<?> factory) {
		register(FactoryKey.DEFAULT_KEY, factory);
	}
	
	public abstract void register(Key key, Factory<?> factory);

	public <T> T instantiate(Type targetType) {
		return instantiate(FactoryKey.DEFAULT_KEY, targetType);
	}
	
	public abstract <T> T instantiate(Key key, Type targetType);

}
