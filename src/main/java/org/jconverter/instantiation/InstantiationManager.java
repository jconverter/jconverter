package org.jconverter.instantiation;

import java.lang.reflect.Type;

import org.jconverter.JConverter;

public abstract class InstantiationManager {

	public static final Object DEFAULT_KEY = new Object();
	
	public void register(Class clazz) {
		register(DEFAULT_KEY, clazz);
	}
	
	public void register(InstanceCreator instanceCreator) {
		register(DEFAULT_KEY, instanceCreator);
	}

	public <T> T instantiate(Type targetType) {
		return instantiate(DEFAULT_KEY, targetType);
	}
	
	public abstract void register(Object key, Class clazz);
	
	public abstract void register(Object key, InstanceCreator instanceCreator);

	public abstract <T> T instantiate(Object key, Type targetType);

}
