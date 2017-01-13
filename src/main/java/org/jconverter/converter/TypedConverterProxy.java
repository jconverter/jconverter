package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.jconverter.util.typewrapper.TypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypedConverterProxy<T,U> extends TypedConverter<T,U> {
	
	private final static Logger logger = LoggerFactory.getLogger(TypedConverterProxy.class);
	
	public static <T,U> TypedConverterProxy<T,U> forConverter(Converter<T,U> converter) {
		Type sourceType;
		Type returnType;
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converter.getClass()).as(Converter.class);
		if(converterTypeWrapper.hasActualTypeArguments()) {
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
			returnType = converterTypeWrapper.getActualTypeArguments()[1];
		} else {
			logger.warn("Converter does not specify parameter types. Source and target types will be assumed the Object class.");
			sourceType = Object.class;
			returnType = Object.class;
		}
		return new TypedConverterProxy<>(converter, sourceType, returnType);
	}
	
	
	private final Converter<T,U> converter;

	public TypedConverterProxy(Converter<T,U> converter, Type sourceType, Type returnType) {
		super(sourceType, returnType);
		this.converter = converter;
	}
	
	public Converter<T,U> getConverter() {
		return converter;
	}

	@Override
	public U apply(T source, Type targetType, JConverter context) {
		return converter.apply(source, targetType, context);
	}
	
}
