package org.jconverter;

import java.lang.reflect.Type;

import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.JGumConverterManager;
import org.jconverter.instantiation.InstantiationManager;
import org.jconverter.instantiation.JGumInstantiationManager;
import org.jgum.JGum;

public class JConverter {
	
	//public static final Object DEFAULT_JCONVERTER_KEY = new Object();
	
	private final InstantiationManager instantiationManager;
	public final ConverterManager converterManager;
	
	public JConverter() {
		JGum jgum = new JGum();
		this.instantiationManager = JGumInstantiationManager.getDefault(jgum);
		this.converterManager = JGumConverterManager.getDefault(jgum);
	}
	
	public JConverter(InstantiationManager instantiationManager, ConverterManager converterManager) {
		this.instantiationManager = instantiationManager;
		this.converterManager = converterManager;
	}
	
	public <T> T convert(Object source, Type targetType) {
		return (T)converterManager.convert(source, targetType, this);
	}
	
	public <T> T instantiate(Object key, Type targetType) {
		return instantiationManager.instantiate(key, targetType);
	}
	
}
