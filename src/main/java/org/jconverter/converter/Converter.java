package org.jconverter.converter;

import static org.jconverter.converter.ConversionDomains.conversionTypes;
import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.typetools.typewrapper.TypeWrapper;

/**
 * Interface implemented by all the JConverter converters.
 * @author sergioc
 *
 * @param <T> the source type.
 * @param <V> the target type.
 */
public interface Converter<T,V> {
	
	/**
	 * 
	 * @param source the object to convert.
	 * @param target the target domain.
	 * @param context the context.
	 * @return the source type converted to the target domain according to the given context.
	 */
	V apply(T source, TypeDomain target, JConverter context);


	static ConversionDomains getConversionDomains(Converter<?,?> converter) {
		Type sourceType;
		Type targetType;
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converter.getClass()).as(Converter.class);
		if (converterTypeWrapper.hasActualTypeArguments()) {
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
			targetType = converterTypeWrapper.getActualTypeArguments()[1];
			return conversionTypes(typeDomain(sourceType), typeDomain(targetType));
		} else {
			/*logger.warn("Converter does not specify parameter types. Source and target types will be assumed the Object class.");
			sourceType = Object.class;
			returnType = Object.class;
			return conversionTypes(sourceType, targetType);*/
			throw new BadConverterException(converter, "Converter does not specify parameter types.");
		}
	}

}
