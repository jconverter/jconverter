package org.jconverter.converter;

import java.lang.reflect.Type;

import org.minitoolbox.reflection.typewrapper.TypeWrapper;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;


public interface Converter<T,V> extends Function<T,V> {

	@Override
	public V apply(T source);

	public V apply(T source, Type targetType);

	/**
	 * TODO delete this class when default methods are available in Java8.
	 * @author sergioc
	 *
	 * @param <T>
	 * @param <V>
	 */
	public abstract class DefaultConverter<T,V> implements Converter<T,V> {
		
		@Override
		public V apply(T source) {
			TypeToken typeToken = new TypeToken<V>(){};
			Type targetType = typeToken.getType();
			return apply(source, (Type)TypeWrapper.wrap(targetType).getRawClass());
		}
		
	}
	
}
