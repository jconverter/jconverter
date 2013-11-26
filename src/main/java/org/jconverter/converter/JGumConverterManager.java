package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.ConverterEvaluator.NonRedundantConverterEvaluator;
import org.jconverter.converter.catalog.StringConverter;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.Category;
import org.jgum.category.Key;
import org.jgum.category.type.TypeCategory;
import org.jgum.strategy.ChainOfResponsibility;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class JGumConverterManager extends ConverterManager {

	private static Logger logger = LoggerFactory.getLogger(JGumConverterManager.class);
	
	public static JGumConverterManager getDefault(JGum jgum) {
		JGumConverterManager converterManager = new JGumConverterManager(jgum);
		converterManager.register(new StringConverter());
		return converterManager;
	}
	
	private final JGum jgum;
	
	public JGumConverterManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(Object converterKey, final Converter converter) {
		final Key key = new Key(converterKey);
		Type converterType = TypeWrapper.wrap(converter.getClass()).asType(Converter.class);
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converterType);
		Type sourceType = null;
		if(converterTypeWrapper.hasActualTypeArguments())
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
		if(sourceType == null || //there are no type arguments or ...
				(sourceType instanceof TypeVariable && TypeVariable.class.cast(sourceType).getBounds().length == 0)) { // ... the type argument is a type variable with empty bounds.
			logger.warn("Converter does not specify a source type. It will be registered at the Object class.");
			sourceType = Object.class;
		}
		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!(sourceTypeWrapper instanceof VariableTypeWrapper)) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateConverterRegister(sourceTypeCategory, key).addFirst(converter);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = jgum.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				getOrCreateConverterRegister(boundTypeCategory, key).addFirst(converter); //set the type solver for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						getOrCreateConverterRegister(category, key).addFirst(converter);
				}
			});
		}
	}
	
	private ConverterRegister getOrCreateConverterRegister(TypeCategory<?> typeCategory, Key key) {
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
	public <T> T convert(Object converterKey, Object object, Type targetType, JConverter context) {
		final Key key = new Key(converterKey);
		Category sourceTypeCategory = jgum.forClass(object.getClass());
		List<ConverterRegister> converterRegisters = sourceTypeCategory.<ConverterRegister>bottomUpProperties(key);
		ChainOfResponsibility chain = new ChainOfResponsibility(converterRegisters, ConversionException.class);
		ConverterEvaluator evaluator = new NonRedundantConverterEvaluator(object, targetType, context);
		return (T) chain.apply(evaluator);
	}		
	
}
