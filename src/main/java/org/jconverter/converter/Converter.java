package org.jconverter.converter;

import java.util.ArrayList;
import java.util.List;

import org.jcategory.strategy.ChainOfResponsibility;
import org.jconverter.JConverter;

/**
 * Interface implemented by all the JConverter converters.
 * @author sergioc
 *
 * @param <T> the source type.
 * @param <V> the target type.
 */
public interface Converter<T,V> {

	public static <T,V> ChainOfResponsibility<Converter<T,V>, V> chainConverters(List<Converter<T,V>> converters) {
		List<Converter<T,V>> conversionFunctions = new ArrayList<>();
		for (Converter<T,V> converter : converters)
			conversionFunctions.add(ConversionFunction.forConverter(converter));
		ChainOfResponsibility<Converter<T,V>,V> chain = new ChainOfResponsibility<>(conversionFunctions);
		return chain;
	}

	/**
	 * 
	 * @param source the object to convert.
	 * @param target the target domain.
	 * @param context the context.
	 * @return the source type converted to the target domain according to the given context.
	 */
	V apply(T source, TypeDomain target, JConverter context);

}
