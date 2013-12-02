package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.ConverterEvaluator.NonRedundantConverterEvaluator;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.Category;
import org.jgum.category.Key;
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
	
	public static class ConverterKey extends Key {
		public ConverterKey(Object key) {
			super(JConverter.DEFAULT_JCONVERTER_KEY);
		}
	}
	
	private final JGum jgum;
	
	public JGumConverterManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(Object unwrappedKey, final Converter converter) {
		final ConverterKey key = new ConverterKey(unwrappedKey);
		Type converterType = TypeWrapper.wrap(converter.getClass()).asType(Converter.class);
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converterType);
		Type sourceType = null;
		if(converterTypeWrapper.hasActualTypeArguments()) {
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
		} else {
			logger.warn("Converter does not specify a source type. It will be registered at the Object class.");
			sourceType = Object.class;
		}

		final TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!sourceTypeWrapper.isVariable()) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateConverterRegister(key, sourceTypeCategory).addFirst(converter);
		} else { //the type argument is a variable type.
			List<TypeCategory<?>> matchedCategories = getMatchingCategories(sourceTypeWrapper);
			for(TypeCategory<?> matchedCategory : matchedCategories) {
				getOrCreateConverterRegister(key, matchedCategory).addFirst(converter); //set the type solver for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					//if(category.isInBoundaries(upperBoundariesClasses))
					if(sourceTypeWrapper.isWeakAssignableFrom(category.getLabel()))
						getOrCreateConverterRegister(key, category).addFirst(converter);
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
	
	private ConverterRegister getOrCreateConverterRegister(ConverterKey key, TypeCategory<?> typeCategory) {
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
	public <T> T convert(Object unwrappedKey, Object source, Type targetType, JConverter context) {
		ConverterKey key = new ConverterKey(unwrappedKey);
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
