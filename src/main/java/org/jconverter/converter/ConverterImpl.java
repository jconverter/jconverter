package org.jconverter.converter;

import static org.jconverter.converter.ConversionGoal.conversionGoal;

import java.util.List;

import org.jconverter.JConverter;
import org.jcategory.JCategory;
import org.jcategory.category.Category;
import org.jcategory.category.Key;

public class ConverterImpl<T,U> implements Converter<T,U> {

	protected final JCategory categorization;
	protected final Key key;
	
/*	public ConverterImpl(JCategory categorization) {
		this(categorization, ConverterKey.DEFAULT_KEY);
	}
	*/
	public ConverterImpl(JCategory categorization, Key key) {
		this.categorization = categorization;
		this.key = key;
	}
	
	@Override
	public U apply(T source, TypeDomain target, JConverter context) {
		ConversionGoal conversionGoal = conversionGoal(source, target);
		List<ConverterRegister> converterRegisters = getConverters(source.getClass());
		try {
			return evalConverters(converterRegisters, conversionGoal, context);
		} catch (NotSuitableConverterException e) {
			NotSuitableConverterException exceptionToThrow = e;
			if (source != e.getConversionGoal().getSource()) {
				exceptionToThrow = new NotSuitableConverterException(conversionGoal, e);
			}
			throw exceptionToThrow;
		} catch (RuntimeException e) {
			ConversionError exceptionToThrow;
			if (e instanceof ConversionError && ((ConversionError) e).getConversionGoal().getSource() == source) {
				exceptionToThrow = (ConversionError) e;
			} else {
				exceptionToThrow = new ConversionError(conversionGoal, e);
			}
			throw exceptionToThrow;
		}
	}
	
	protected U evalConverters(List<ConverterRegister> converterRegisters, ConversionGoal conversionGoal, JConverter context) {
		ConverterChain<ConverterRegister,U> chain = new ConverterChain.TopLevelChain<>(conversionGoal, converterRegisters);
		ConverterEvaluator converterEvaluator = new InterTypeConverterEvaluator<>(conversionGoal, context);
		ConverterRegisterEvaluator evaluator = new ConverterRegisterEvaluator(
				new NonRedundantConverterEvaluator(converterEvaluator));
		return (U) chain.apply(evaluator);
	}
	
	protected List<ConverterRegister> getConverters(Class<?> clazz) {
		Category sourceTypeCategory = categorization.forClass(clazz);
		List<ConverterRegister> converterRegisters = sourceTypeCategory.<ConverterRegister>bottomUpProperties(key);
		return converterRegisters;
	}
	
}