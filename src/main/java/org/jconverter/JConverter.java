package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.instantiation.InstantiationManager;

public class JConverter {

	private final InstantiationManager instantiationManager;
	public final ConverterManager converterManager;
	
	public JConverter(InstantiationManager instantiationManager, ConverterManager converterManager) {
		this.instantiationManager = instantiationManager;
		this.converterManager = converterManager;
	}
	
	public <T> T instantiate(Object key, Type targetType) {
		return instantiationManager.instantiate(key, targetType);
	}
	
}
