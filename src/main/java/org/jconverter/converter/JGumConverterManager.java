package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.ConverterEvaluator.NonRedundantConverterEvaluator;
import org.jconverter.converter.catalog.ObjectToStringConverter;
import org.jconverter.converter.catalog.array.ArrayToArrayConverter;
import org.jconverter.converter.catalog.array.ArrayToCollectionConverter;
import org.jconverter.converter.catalog.array.ArrayToEnumerationConverter;
import org.jconverter.converter.catalog.array.ArrayToIterableConverter;
import org.jconverter.converter.catalog.array.ArrayToIteratorConverter;
import org.jconverter.converter.catalog.array.ArrayToMapConverter;
import org.jconverter.converter.catalog.calendar.CalendarToNumberConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToArrayConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToCollectionConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToEnumerationConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToIterableConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToIteratorConverter;
import org.jconverter.converter.catalog.enumeration.EnumerationToMapConverter;
import org.jconverter.converter.catalog.iterable.IterableToArrayConverter;
import org.jconverter.converter.catalog.iterable.IterableToCollectionConverter;
import org.jconverter.converter.catalog.iterable.IterableToEnumerationConverter;
import org.jconverter.converter.catalog.iterable.IterableToIterableConverter;
import org.jconverter.converter.catalog.iterable.IterableToIteratorConverter;
import org.jconverter.converter.catalog.iterable.IterableToMapConverter;
import org.jconverter.converter.catalog.iterator.IteratorToArrayConverter;
import org.jconverter.converter.catalog.iterator.IteratorToCollectionConverter;
import org.jconverter.converter.catalog.iterator.IteratorToEnumerationConverter;
import org.jconverter.converter.catalog.iterator.IteratorToIterableConverter;
import org.jconverter.converter.catalog.iterator.IteratorToIteratorConverter;
import org.jconverter.converter.catalog.iterator.IteratorToMapConverter;
import org.jconverter.converter.catalog.map.MapToArrayConverter;
import org.jconverter.converter.catalog.map.MapToCollectionConverter;
import org.jconverter.converter.catalog.map.MapToEnumerationConverter;
import org.jconverter.converter.catalog.map.MapToIterableConverter;
import org.jconverter.converter.catalog.map.MapToIteratorConverter;
import org.jconverter.converter.catalog.map.MapToMapConverter;
import org.jconverter.converter.catalog.number.NumberToBooleanConverter;
import org.jconverter.converter.catalog.number.NumberToCalendarConverter;
import org.jconverter.converter.catalog.number.NumberToGregorianCalendarConverter;
import org.jconverter.converter.catalog.number.NumberToNumberConverter;
import org.jconverter.converter.catalog.number.NumberToXMLGregorianCalendarConverter;
import org.jconverter.converter.catalog.string.StringToBooleanConverter;
import org.jconverter.converter.catalog.string.StringToCalendarConverter;
import org.jconverter.converter.catalog.string.StringToCharacterConverter;
import org.jconverter.converter.catalog.string.StringToDateConverter;
import org.jconverter.converter.catalog.string.StringToGregorianCalendarConverter;
import org.jconverter.converter.catalog.string.StringToNumberConverter;
import org.jconverter.converter.catalog.string.StringToXMLGregorianCalendarConverter;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.Category;
import org.jgum.category.type.TypeCategory;
import org.jgum.strategy.ChainOfResponsibility;
import org.minitoolbox.collections.ArrayIterator;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class JGumConverterManager extends ConverterManager {

	private static Logger logger = LoggerFactory.getLogger(JGumConverterManager.class);
	
	private final JGum jgum;
	
	public JGumConverterManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(final Object key, final Converter converter) {
		Type converterType = TypeWrapper.wrap(converter.getClass()).asType(Converter.class);
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converterType);
		Type sourceType = null;
		if(converterTypeWrapper.hasActualTypeArguments())
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
		else {
			logger.warn("Converter does not specify a source type. It will be registered at the Object class.");
			sourceType = Object.class;
		}

		final TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!sourceTypeWrapper.isVariable()) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateConverterRegister(sourceTypeCategory, key).addFirst(converter);
		} else { //the type argument is a variable type.
			List<TypeCategory<?>> matchedCategories = getMatchingCategories(sourceTypeWrapper);
			for(TypeCategory<?> matchedCategory : matchedCategories) {
				getOrCreateConverterRegister(matchedCategory, key).addFirst(converter); //set the type solver for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					//if(category.isInBoundaries(upperBoundariesClasses))
					if(sourceTypeWrapper.isWeakAssignableFrom(category.getLabel()))
						getOrCreateConverterRegister(category, key).addFirst(converter);
				}
			});
		}
	}
	
	private List<TypeCategory<?>> getMatchingCategories(TypeWrapper wrappedType) {
		if(wrappedType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) wrappedType;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = jgum.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			return boundTypeCategories;
		} else if(wrappedType instanceof ArrayTypeWrapper) {
			List<TypeCategory<?>> compatibleCategories = new ArrayList<>();
			List<TypeCategory<?>> objectChildren = jgum.forClass(Object.class).getChildren();
			for(TypeCategory<?> objectChild : objectChildren) {
				if(wrappedType.isWeakAssignableFrom(objectChild.getLabel()))
					compatibleCategories.add(objectChild);
			}
			return compatibleCategories;
		} else
			throw new RuntimeException(); //this should not happen
	}
	
	private ConverterRegister getOrCreateConverterRegister(TypeCategory<?> typeCategory, Object key) {
		Optional<ConverterRegister> chainOpt = typeCategory.getLocalProperty(key);
		ConverterRegister chain;
		if(chainOpt.isPresent()) {
			chain =  chainOpt.get();
		} else {
			chain = new ConverterRegister(jgum);
			typeCategory.setProperty(key, chain);
		}
		return chain;
	}
	
	@Override
	public <T> T convert(Object key, Object source, Type targetType, JConverter context) {
		Category sourceTypeCategory = jgum.forClass(source.getClass());
		List<ConverterRegister> converterRegisters = sourceTypeCategory.<ConverterRegister>bottomUpProperties(key);
		ChainOfResponsibility chain = new ChainOfResponsibility(converterRegisters, ConversionException.class);
		ConverterEvaluator evaluator = new NonRedundantConverterEvaluator(source, targetType, context);
		try {
			return (T) chain.apply(evaluator);
		} catch(ConversionException e) {
			TypeWrapper sourceWrappedType = TypeWrapper.wrap(source.getClass());
			//if the source object is an array of primitives
			if(sourceWrappedType instanceof ArrayTypeWrapper && sourceWrappedType.getBaseType() instanceof Class && ((Class)sourceWrappedType.getBaseType()).isPrimitive()) 
				return convert(key, new ArrayIterator(source), targetType, context);
			else
				throw e;
		}
	}		
	
}
