package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.minitoolbox.reflection.IncompatibleTypesException;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

import com.google.common.base.Function;

public class ConverterEvaluator<T,U> implements Function<Converter<T,U>,U> {

	protected final T sourceObject;
	protected final Type targetType;
	protected final JConverter context;
	
	public ConverterEvaluator(T sourceObject, Type targetType, JConverter context) {
		this.sourceObject = sourceObject;
		this.targetType = targetType;
		this.context = context;
	}
	
	@Override
	public U apply(Converter<T, U> converter) {
		TypedConverter<T,U> typedConverter = TypedConverter.forConverter(converter);
		Type bestTypeForConverter;
		try {
			bestTypeForConverter = TypeWrapper.wrap(targetType).mostSpecificType(typedConverter.getReturnType()); //will throw an exception if the types are not compatible
		} catch(IncompatibleTypesException e) {
			throw new ConversionException();
		}
		return typedConverter.apply(sourceObject, bestTypeForConverter, context);
		
		//return typedConverter.apply(sourceObject, targetType, context);
	}

}
