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
	
	/**
	 * 
	 * @param proxiedJConverter the proxied context.
	 * @param defaultKey the key constraining the registered converters, instance creators, and type solvers that will be looked up by default (i.e., if no key is specified).
	 */
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
