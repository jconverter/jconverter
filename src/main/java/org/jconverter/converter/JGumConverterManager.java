package org.jconverter.converter;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jconverter.JConverter;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.type.TypeCategory;
import org.minitoolbox.collections.ArrayIterator;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.primitives.Primitives;

public class JGumConverterManager extends ConverterManager {

	private static final Logger logger = LoggerFactory.getLogger(JGumConverterManager.class);
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return an instance of JGumConverterManager configured with default converters.
	 */
	public static JGumConverterManager createDefault(JGum jgum) {
		JGumConverterManager converterManager = new JGumConverterManager(jgum);
		registerDefaults(converterManager);
		return converterManager;
	}
	
	protected final JGum jgum;
	
	public JGumConverterManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	private List<TypeCategory<?>> getMatchingCategories(TypeWrapper wrappedType) {
		if(wrappedType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) wrappedType;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
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
	
	private ConverterRegister getOrCreateConverterRegister(Object key, TypeCategory<?> typeCategory) {
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
	public void register(final Object key, final Converter<?,?> converter) {
		final TypedConverter<?,?> typedConverter = TypedConverter.forConverter(converter);

		Type sourceType = typedConverter.getSourceType();
		final TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!sourceTypeWrapper.isVariable()) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateConverterRegister(key, sourceTypeCategory).addFirst(typedConverter);
		} else { //the type argument is a variable type.
			List<TypeCategory<?>> matchedCategories = getMatchingCategories(sourceTypeWrapper);
			for(TypeCategory<?> matchedCategory : matchedCategories) {
				getOrCreateConverterRegister(key, matchedCategory).addFirst(typedConverter); //set the type solver for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					//if(category.isInBoundaries(upperBoundariesClasses))
					if(sourceTypeWrapper.isWeakAssignableFrom(category.getLabel()))
						getOrCreateConverterRegister(key, category).addFirst(typedConverter);
				}
			});
		}
	}

	
	@Override
	public <T> T convert(Object key, Object source, Type targetType, JConverter context) {
		JGumConverter jGumConverter = new JGumConverter(jgum, key);
		return convert(jGumConverter, source, targetType, context);
	}		
	
	protected <T> T convert(JGumConverter jGumConverter, Object source, Type targetType, JConverter context) {
		try {
			return (T) jGumConverter.apply(source, targetType, context);
		} catch(ConversionException e) {
			TypeWrapper targetWrappedType = TypeWrapper.wrap(targetType);
			
			if(targetWrappedType.isPrimitive()) {
				return convert(jGumConverter, source, Primitives.wrap(targetWrappedType.getRawClass()), context); //inboxing of the target type
			}
				
			TypeWrapper sourceWrappedType = TypeWrapper.wrap(source.getClass());
			//if the source object is an array of primitives
			if(sourceWrappedType instanceof ArrayTypeWrapper && sourceWrappedType.getBaseType() instanceof Class && ((Class)sourceWrappedType.getBaseType()).isPrimitive()) {
				return convert(jGumConverter, new ArrayIterator(source), targetType, context);
			}
			throw e;
		}
	}
	
}
