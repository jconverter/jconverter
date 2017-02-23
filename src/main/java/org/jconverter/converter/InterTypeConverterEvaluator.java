package org.jconverter.converter;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;

import org.jconverter.JConverter;
import org.typetools.IncompatibleTypesException;
import org.typetools.typewrapper.TypeWrapper;
import org.typetools.typewrapper.VariableTypeWrapper;

public class InterTypeConverterEvaluator<T,U> extends ConverterEvaluator<T,U> {

	protected final JConverter context;
	
	public InterTypeConverterEvaluator(ConversionGoal conversionGoal, JConverter context) {
		super(conversionGoal);
		this.context = context;
	}

	/**
	 * Evaluate a converter with the evaluator source object, target type and context.
	 */
	@Override
	public U apply(Converter<T, U> converter) {
		ConversionFunction<T,U> conversionFunction = ConversionFunction.forConverter(converter);
		Type bestTypeForConverter; //the inferred best target type for the conversion.
		try {
			bestTypeForConverter = TypeWrapper.wrap(getConversionGoal().getTarget().getType()).mostSpecificType(
					conversionFunction.getRange().getType()); //will throw an exception if the types are not compatible
		} catch(IncompatibleTypesException e) {
			throw new DelegateConversionException(getConversionGoal());
		}
		TypeWrapper wrappedBestType = TypeWrapper.wrap(bestTypeForConverter);

		//The best conversion target type is a variable type. This means that the target type is not assignable to the converter return type (but the contrary is true).
		if(wrappedBestType instanceof VariableTypeWrapper) { 
			Type upperBounds[] = ((VariableTypeWrapper) wrappedBestType).getUpperBounds();
			if(upperBounds.length == 1) {
				bestTypeForConverter = upperBounds[0];
			} else {
				throw new DelegateConversionException(getConversionGoal()); //impossible to infer best type for converter (the best type is a variable type with multiple upper bounds).
			}
		}
		return conversionFunction.apply((T) getConversionGoal().getSource(), typeDomain(bestTypeForConverter), context);
	}

}
