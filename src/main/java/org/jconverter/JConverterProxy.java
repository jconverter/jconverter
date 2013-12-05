package org.jconverter;

import java.lang.reflect.Type;

/**
 * A proxy for JConverter contexts.
 * @author sergioc
 *
 */
public class JConverterProxy extends JConverter {

	private final JConverter proxiedJConverter;
	
	/**
	 * 
	 * @param proxiedJConverter the proxied context.
	 */
	public JConverterProxy(JConverter proxiedJConverter) {
		this.proxiedJConverter = proxiedJConverter;
	}

	@Override
	public <T> T convert(Object source, Type targetType) {
		return proxiedJConverter.convert(source, targetType);
	}
	
	@Override
	public <T> T convert(Object key, Object source, Type targetType) {
		return proxiedJConverter.convert(key, source, targetType);
	}
	
	@Override
	public <T> T instantiate(Type targetType) {
		return proxiedJConverter.instantiate(targetType);
	}
	
	@Override
	public <T> T instantiate(Object key, Type targetType) {
		return proxiedJConverter.instantiate(key, targetType);
	}
	
	@Override
	public Type getType(Object object) {
		return proxiedJConverter.getType(object);
	}
	
	@Override
	public Type getType(Object key, Object object) {
		return proxiedJConverter.getType(key, object);
	}

}
