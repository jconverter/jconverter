package org.jconverter.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.jgum.JGum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterRegister {

	private final static Logger logger = LoggerFactory.getLogger(ConverterRegister.class);
	
	private final List<ConversionFunction<?,?>> conversionFunctions;
	private final JGum jgum;
	
	public ConverterRegister(JGum jgum) {
		conversionFunctions = new ArrayList<>();
		this.jgum = jgum;
	}
	
	public void addFirst(ConversionFunction<?,?> conversionFunction) {
		conversionFunctions.add(0, conversionFunction);
	}
	
	public List<ConversionFunction<?,?>> orderedConverters(TypeDomain target) {
		TreeSet<ComparableConversionFunction> comparableConversionFunctions = new TreeSet<>(); //TreeSet will keep the natural ordering of its members.
		for (int i = 0; i< conversionFunctions.size(); i++) {
			ConversionFunction<?,?> conversionFunction = conversionFunctions.get(i);
			if (conversionFunction.getRange().isSubsetOf(target)) {
				ComparableConversionFunction comparableConversionFunction = new ComparableConversionFunction(conversionFunction, i, target);
				comparableConversionFunctions.add(comparableConversionFunction);
			}
		}
		List<ConversionFunction<?,?>> converters = new ArrayList<>();
		for (ComparableConversionFunction conversionFunction : comparableConversionFunctions) {
			converters.add(conversionFunction.conversionFunction);
		}
		return converters;
	}
	

	
	private class ComparableConversionFunction implements Comparable<ComparableConversionFunction> {

		private final ConversionFunction<?,?> conversionFunction;
		private final int index;
		private final int distanceToTarget;

		/**
		 * The constructor assumes that the processed converter is compatible with the target type. No further verifications are accomplished.
		 * @param typedConverter a processed converter.
		 * @param index the index of the converter in the converter register.
		 * @param target the target conversion type.

		 */
		public ComparableConversionFunction(ConversionFunction<?,?> typedConverter, int index, TypeDomain target) {
			this.conversionFunction = typedConverter;
			this.index = index;
			if (typedConverter.getRange().hasVariableType()) {//the converter has different target types (quantified with upper bounds).
				distanceToTarget = 0; //assuming the target type is compatible with the conversionFunction, the converter return type can be the current target type.
			} else {
				distanceToTarget = jgum.forClass(typedConverter.getRange().getRawClass()).distance(target.getRawClass());
			}
		}
		
		@Override
		public int compareTo(ComparableConversionFunction contextedConverter) {
			if (distanceToTarget != contextedConverter.distanceToTarget) {
				return distanceToTarget - contextedConverter.distanceToTarget;
			} else {
				return index - contextedConverter.index;
			}
		}
		
	}
	
}
