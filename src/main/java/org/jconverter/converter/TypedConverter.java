package org.jconverter.converter;

/**
 * A converter that explicitly specifies its source and target types.
 * @author sergioc
 *
 * @param <T> the source type of the converter.
 * @param <U> the target type of the converter.
 */
public abstract class TypedConverter<T,U> implements Converter<T,U> {

	public static <T,U> TypedConverter<T,U> forConverter(Converter<T,U> converter) {
		if (converter instanceof TypedConverter) {
			return (TypedConverter<T, U>) converter;
		} else {
			return TypedConverterProxy.forConverter(converter);
		}
	}

	public static <T,U> TypedConverter<T,U> forConverter(Converter<T,U> converter,
														 ConversionDomains<TypeDomain, TypeDomain> conversionTypes) {
		return new TypedConverterProxy<>(converter, conversionTypes);
	}

	private final ConversionDomains<TypeDomain, TypeDomain> conversionTypes;
	
	protected TypedConverter(ConversionDomains<TypeDomain, TypeDomain> conversionTypes) {
		this.conversionTypes = conversionTypes;
	}

	public ConversionDomains<TypeDomain, TypeDomain> getConversionDomains() {
		return conversionTypes;
	}

}
