package org.jconverter.instantiation;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jgum.category.Key;

public interface InstantiationManager {
	
	public static class InstantiationKey extends Key {
		public InstantiationKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	public static final Object DEFAULT_KEY = new InstantiationKey(JConverter.DEFAULT_JCONVERTER_KEY);
	
	public abstract void register(Object key, Class clazz);
	
	public abstract void register(Object key, InstanceCreator instanceCreator);

	public abstract <T> T instantiate(Object key, Type targetType);

}
