package org.jconverter.converter;

import org.jconverter.JConverter;

public class TypedConverterProxy<T,U> extends TypedConverter<T,U> {

	public static <T,U> TypedConverterProxy<T,U> forConverter(Converter<T,U> converter) {
		if (converter instanceof TypedConverterProxy) {
			return (TypedConverterProxy) converter;
		} else {
			return new TypedConverterProxy(converter);
		}
	}

	public static <T,U> TypedConverterProxy<T,U> forConverter(Converter<T,U> converter, ConversionDomains<TypeDomain, TypeDomain> conversionTypes) {
		return new TypedConverterProxy<>(converter, conversionTypes);
	}


	private final Converter<T,U> converter;

	public TypedConverterProxy(Converter<T,U> converter) {
		this(converter, Converter.getConversionDomains(converter));
	}

	public TypedConverterProxy(Converter<T,U> converter, ConversionDomains conversionTypes) {
		super(conversionTypes);
		this.converter = converter;
	}
	
	public Converter<T,U> getConverter() {
		return converter;
	}

	@Override
	public U apply(T source, TypeDomain target, JConverter context) {
		return converter.apply(source, target, context);
	}
	
}
