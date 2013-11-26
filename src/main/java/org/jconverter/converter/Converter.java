package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

import com.google.common.reflect.TypeToken;


public interface Converter<T,V> {

	public V apply(T source, JConverter context);

	public V apply(T source, Type targetType, JConverter context);

	/**
	 * @author sergioc
	 *
	 * @param <T>
	 * @param <V>
	 */
	public abstract class DefaultConverter<T,V> implements Converter<T,V> {
		
		private final Type targetType;
		
		public DefaultConverter() {
			targetType = new TypeToken<V>(getClass()){}.getType();
		}

		@Override
		public final V apply(T source, JConverter context) {
			return apply(source, (Type)TypeWrapper.wrap(targetType).getRawClass(), context);
		}
	}
	
}
