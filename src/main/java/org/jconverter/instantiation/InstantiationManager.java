package org.jconverter.instantiation;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;

public abstract class InstantiationManager {
	
	public static class InstantiationKey extends Key {
		public InstantiationKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new InstantiationKey(JConverter.DEFAULT_JCONVERTER_KEY);
	

	public void register(Class clazz) {
		register(DEFAULT_KEY, clazz);
	}
	
	public abstract void register(Object key, Class clazz);
	
	public void register(InstanceCreator instanceCreator) {
		register(DEFAULT_KEY, instanceCreator);
	}
	
	public abstract void register(Object key, InstanceCreator instanceCreator);

	public <T> T instantiate(Type targetType) {
		return instantiate(targetType, targetType);
	}
	
	public abstract <T> T instantiate(Object key, Type targetType);

}
