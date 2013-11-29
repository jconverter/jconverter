package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

import com.google.common.reflect.TypeToken;


public interface Converter<T,V> {

	public V apply(T source, Type targetType, JConverter context);
	
}
