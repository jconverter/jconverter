package org.jconverter.converter;

import static org.jconverter.converter.ConversionGoal.conversionGoal;
import static org.jconverter.converter.TypeDomain.typeDomain;

import org.jconverter.JConverter;

public class CheckedConverterEvaluator<T, U> extends InterTypeConverterEvaluator<T, U> {

	public CheckedConverterEvaluator(ConversionGoal conversionGoal, JConverter context) {
		super(conversionGoal, context);
	}

	@Override
	public U apply(Converter<T,U> converter) {
		TypedConverter<T,U> typedConverter = TypedConverter.forConverter(converter);
		//checking the source type only, the target is checked in the super call.
		if (typeDomain(getConversionGoal().getSource().getClass()).isSubsetOf(typedConverter.getConversionDomains().getSource())) {
			return super.apply(typedConverter);
		} else {
			throw new DelegateConversionException(conversionGoal(getConversionGoal().getSource(),
					typeDomain(typedConverter.getConversionDomains().getTarget().getType())));
		}
	}
	
}
