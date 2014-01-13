package org.jconverter.converter;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.minitoolbox.reflection.IncompatibleTypesException;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

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
	
	/**
	 * Evaluate a converter with the evaluator source object, target type and context.
	 */
	@Override
	public U apply(Converter<T, U> converter) {
		TypedConverter<T,U> typedConverter = TypedConverter.forConverter(converter);
		Type bestTypeForConverter; //the inferred best target type for the conversion.
		try {
			bestTypeForConverter = TypeWrapper.wrap(targetType).mostSpecificType(typedConverter.getReturnType()); //will throw an exception if the types are not compatible
		} catch(IncompatibleTypesException e) {
			throw new ConversionException();
		}
		TypeWrapper wrappedBestType = TypeWrapper.wrap(bestTypeForConverter);
		//The best conversion target type is a variable type. This means that the target type is not assignable to the converter return type (but the contrary is true).
		if(wrappedBestType instanceof VariableTypeWrapper) { 
			Type upperBounds[] = ((VariableTypeWrapper) wrappedBestType).getUpperBounds();
			if(upperBounds.length == 1)
				bestTypeForConverter = upperBounds[0];
			else
				throw new ConversionException(); //impossible to infer best type for converter (the best type is a variable type with multiple upper bounds).
		}
		return typedConverter.apply(sourceObject, bestTypeForConverter, context);
	}

}
