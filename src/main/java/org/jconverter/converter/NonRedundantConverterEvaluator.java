package org.jconverter.converter;

import org.jconverter.util.NonRedundantEvaluator;

public class NonRedundantConverterEvaluator<T, U> extends ConverterEvaluator<T, U> {

	private final NonRedundantEvaluator<Converter<T, U>, U> nonRedundantEvaluator;

	public NonRedundantConverterEvaluator(ConverterEvaluator<T, U> evaluator) {
		super(evaluator.getConversionGoal());
		this.nonRedundantEvaluator = new NonRedundantEvaluator<>(evaluator);
	}
	
	@Override
	public U apply(Converter<T, U> converter) {
		try {
			return nonRedundantEvaluator.apply(converter);
		} catch(NonRedundantEvaluator.AlreadyEvaluatedException e) {
			throw new DelegateConversionException(getConversionGoal());
		}
	}

}