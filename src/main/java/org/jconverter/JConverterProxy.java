package org.jconverter;

import java.lang.reflect.Type;

public class JConverterProxy extends JConverter {

	private final JConverter proxiedJConverter;
	
	public JConverterProxy(JConverter proxiedJConverter) {
		this.proxiedJConverter = proxiedJConverter;
	}
	
	public JConverterProxy(JConverter proxiedJConverter, Object defaultKey) {
		this.proxiedJConverter = proxiedJConverter;
		this.defaultKey = defaultKey;
	}

	@Override
	public <T> T convert(Object key, Object source, Type targetType) {
		return proxiedJConverter.convert(key, source, targetType);
	}
	
	@Override
	public <T> T instantiate(Object key, Type targetType) {
		return proxiedJConverter.instantiate(key, targetType);
	}
	
	@Override
	public Type getType(Object key, Object object) {
		return proxiedJConverter.getType(key, object);
	}

}
