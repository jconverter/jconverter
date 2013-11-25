package org.jconverter.instantiation;

import java.lang.reflect.Type;

public interface InstantiationManager {

	public void register(Object key, Class clazz);
	
	public void register(Object key, InstanceCreator instanceCreator);

	public <T> T instantiate(Object key, Type targetType);

}
