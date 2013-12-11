package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;

public class CheckConverterEvaluator<T, U> extends ConverterEvaluator<T, U> {

	public CheckConverterEvaluator(T sourceObject, Type targetType, JConverter context) {
		super(sourceObject, targetType, context);
	}

	@Override
	public U apply(Converter<T,U> converter) {
		TypedConverter<T,U> typedConverter = TypedConverter.forConverter(converter);
		//checking the source type only, the target is checked in the super call.
		if(typedConverter.isSourceTypeCompatible(sourceObject.getClass()))
			return super.apply(typedConverter);
		else
			throw new ConversionException();
	}
	
}
