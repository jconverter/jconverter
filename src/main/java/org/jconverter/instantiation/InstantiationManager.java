package org.jconverter.instantiation;

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

import org.jconverter.JConverter;
import org.jgum.JGum;

public abstract class InstantiationManager {
	
	public static final Object DEFAULT_KEY = new Object();
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return the default instantiation manager.
	 */
	public static InstantiationManager getDefault(JGum jgum) {
		JGumInstantiationManager instantiationManager = new JGumInstantiationManager(jgum);
		instantiationManager.register(ArrayDeque.class);
		instantiationManager.register(HashMap.class);
		instantiationManager.register(HashSet.class);
		instantiationManager.register(ArrayList.class);
		instantiationManager.register(new InstanceCreator<Calendar>() {
			@Override
			public Calendar instantiate(Type type) {
				return Calendar.getInstance(); //"Gets a calendar using the default time zone and locale. The Calendar returned is based on the current time in the default time zone with the default locale."
			}
		});
		instantiationManager.register(new InstanceCreator<DateFormat>() {
			@Override
			public DateFormat instantiate(Type type) {
				return new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			}
		});
		instantiationManager.register(new InstanceCreator<NumberFormat>() {
			@Override
			public NumberFormat instantiate(Type type) {
				return NumberFormat.getInstance(); //"a general-purpose number format for the current default locale."
			}
		});
		return instantiationManager;
	}
	
	
	public void register(Class clazz) {
		register(DEFAULT_KEY, clazz);
	}
	
	public abstract void register(Object key, Class clazz);
	
	public void register(List<Class> classes, InstanceCreator instanceCreator) {
		register(DEFAULT_KEY, classes, instanceCreator);
	}
	
	public abstract void register(Object key, List<Class> classes, InstanceCreator instanceCreator);
	
	
	public void register(InstanceCreator instanceCreator) {
		register(DEFAULT_KEY, instanceCreator);
	}
	
	public abstract void register(Object key, InstanceCreator instanceCreator);

	public <T> T instantiate(Type targetType) {
		return instantiate(targetType, targetType);
	}
	
	public abstract <T> T instantiate(Object key, Type targetType);

}
