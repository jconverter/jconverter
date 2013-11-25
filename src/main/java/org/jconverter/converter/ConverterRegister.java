package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jconverter.typesolver.JGumTypeSolverManager;
import org.jgum.JGum;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

import com.google.common.base.Function;

public class ConverterRegister {

	private final static Logger logger = Logger.getLogger(JGumTypeSolverManager.class);
	
	private final List<ProcessedConverter> processedConverters;
	private final JGum jgum;
	
	public ConverterRegister(JGum jgum) {
		processedConverters = new ArrayList<>();
		this.jgum = jgum;
	}
	
	public void addFirst(Converter function) {
		processedConverters.add(0, new ProcessedConverter(function));
	}
	
	public List<Converter> orderedConverters(Type targetType) {
		TreeSet<ContextedConverter> contextedConverters = new TreeSet<>();
		for(int i = 0; i<processedConverters.size(); i++) {
			ProcessedConverter processedConverter = processedConverters.get(i);
			ContextedConverter contextedConverter = new ContextedConverter(processedConverter, i, jgum, targetType);
			contextedConverters.add(contextedConverter);
		}
		List<Converter> converters = new ArrayList<>();
		for(ContextedConverter contextedConverter : contextedConverters) {
			converters.add(contextedConverter.getConverter());
		}
		return converters;
	}
	
	
	private class ProcessedConverter {
		private final Converter converter;
		private final Type returnType;
		private final Class<?> returnClass;
		private final List<Class<?>> upperBounds;
		
		public ProcessedConverter(Converter converter) {
			this.converter = converter;
			TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converter.getClass()).as(Function.class);
			Type returnType = null;
			if(converterTypeWrapper.hasActualTypeArguments())
				returnType = converterTypeWrapper.getActualTypeArguments()[1];
			if(returnType == null || //there are no type arguments or ...
					(returnType instanceof TypeVariable && TypeVariable.class.cast(returnType).getBounds().length == 0)) { // ... the type argument is a type variable with empty bounds.
				logger.warn("Converter does not specify a targer type. Target will be assumed the Object class.");
				returnType = Object.class;
			}
			this.returnType = returnType;
			TypeWrapper targetTypeWrapper = TypeWrapper.wrap(returnType);
			if(!(targetTypeWrapper instanceof VariableTypeWrapper)) {
				returnClass = targetTypeWrapper.getRawClass();
				upperBounds = null;
			} else { //the type argument is a TypeVariable with non-empty bounds.
				VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) targetTypeWrapper;
				upperBounds = TypeUtil.asRawClasses(asList(variableTypeWrapper.getUpperBounds()));
				returnClass = null;
			}
		}
		
		public boolean hasBoundTargetType() {
			return returnClass == null;
		}
		
		public List<Class<?>> getUpperBounds() {
			return upperBounds;
		}

		public Class<?> getReturnClass() {
			return returnClass;
		}
		
		public boolean isCompatible(Type type) {
			return TypeWrapper.wrap(type).isWeakAssignableFrom(returnType);
		}
		
		public Converter getConverter() {
			return converter;
		}
	}

	
	private class ContextedConverter implements Comparable<ContextedConverter> {

		private final Type targetType;
		private final ProcessedConverter processedConverter;
		private final int distanceToTarget;
		private final int index;
		
		/**
		 * The constructor assumes that the processed converter is compatible with the target type. No further verifications are accomplished.
		 * @param targetType the target conversion type.
		 * @param processedConverter a processed coverter.
		 * @param jgum a jgum context.
		 */
		public ContextedConverter(ProcessedConverter processedConverter, int index, JGum jgum, Type targetType) {
			this.targetType = targetType;
			this.processedConverter = processedConverter;
			if(processedConverter.hasBoundTargetType()) //the converter has different target types (quantified with upper bounds).
				distanceToTarget = 0; //assuming the target type is compatible with the processedConverter, the converter return type can be the current target type.
			else {
				Class targetClass = TypeWrapper.wrap(targetType).getRawClass();
				distanceToTarget = jgum.forClass(processedConverter.getReturnClass()).distance(targetClass);
			}
			this.index = index;
		}

		public Converter getConverter() {
			return processedConverter.getConverter();
		}
		
		@Override
		public int compareTo(ContextedConverter contextedConverter) {
			if(distanceToTarget != contextedConverter.distanceToTarget)
				return distanceToTarget - contextedConverter.distanceToTarget;
			else
				return index - contextedConverter.index;
		}
		
	}
	
}

