package org.jconverter.factory;

import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jcategory.JCategory;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.Key;
import org.jcategory.category.type.TypeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typeutils.TypeUtils;
import org.typeutils.typewrapper.TypeWrapper;
import org.typeutils.typewrapper.VariableTypeWrapper;

/**
 * Utility class mapping types to instance creators.
 * @author sergioc
 *
 */
public class FactoryManagerImpl extends FactoryManager {
	
	private final static Logger logger = LoggerFactory.getLogger(FactoryManagerImpl.class);
	
	/**
	 * @param categorization a JCategory categorization context.
	 * @return an instance of FactoryManager configured with default instance creators.
	 */
	public static FactoryManagerImpl createDefault(JCategory categorization) {
		FactoryManagerImpl instantiationManager = new FactoryManagerImpl(categorization);
		registerDefaults(instantiationManager);
		return instantiationManager;
	}
	
	private final JCategory categorization;
	
	public FactoryManagerImpl(JCategory categorization) {
		this.categorization = categorization;
	}
	
	private FactoryChain getOrCreateChain(Key key, TypeCategory<?> typeCategory) {
		FactoryChain chain;
		List<FactoryChain> chains = typeCategory.getLocalProperty(key);
		if (chains.isEmpty()) {
			chain = new FactoryChain(new ArrayList<>());
			typeCategory.setProperty(key, chain);
		} else {
			chain = chains.get(0);
		}
		return chain;
	}
	
	@Override
	public void register(Key key, Class<?> clazz) {
		if(Modifier.isAbstract(clazz.getModifiers()))
			throw new RuntimeException(clazz.getName() + " should not be abstract.");
		List<TypeCategory<?>> abstractAncestors = (List) categorization.forClass(clazz).getAbstractAncestors();
		for(TypeCategory<?> abstractAncestor : abstractAncestors) {
			getOrCreateChain(key, abstractAncestor).addFirst(new InstantiationClassFactory(clazz));
		}
	}

	@Override
	public void register(Key key, List<Class<?>> classes, Factory<?> factory) {
		for(Class<?> clazz : classes) {
			TypeCategory<?> typeCategory = categorization.forClass(clazz);
			getOrCreateChain(key, typeCategory).addFirst(factory);
		}
	}
	
	@Override
	public void register(Key key, Factory<?> factory) {
		Type factoryType = TypeWrapper.wrap(factory.getClass()).asType(Factory.class);
		TypeWrapper factoryTypeWrapper = TypeWrapper.wrap(factoryType);
		Type sourceType = null;
		if(factoryTypeWrapper.hasActualTypeArguments()) {
			sourceType = factoryTypeWrapper.getActualTypeArguments()[0];
		} else {
			throw new RuntimeException("Instance creator does not specify a source type.");
		}
		
		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!(sourceTypeWrapper instanceof VariableTypeWrapper)) {
			TypeCategory<?> sourceTypeCategory = categorization.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateChain(key, sourceTypeCategory).addFirst(factory);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtils.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = categorization.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				getOrCreateChain(key, boundTypeCategory).addFirst(factory); //set the factory for all the known types that are in the boundaries.
			}
			categorization.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the factory for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						getOrCreateChain(key, category).addFirst(factory);
				}
			});
		}
	}

	@Override
	public <T> T instantiate(Key key, Type targetType) {
		TypeCategory<?> sourceTypeCategory = categorization.forClass(TypeWrapper.wrap(targetType).getRawClass());
		FactoryChain<T> chain = getOrCreateChain(key, sourceTypeCategory);
		FactoryEvaluator<T> factoryEvaluator = new FactoryEvaluator<>(targetType);
		FactoryChainEvaluator<T> evaluator = new FactoryChainEvaluator<>(factoryEvaluator);
		return (T)chain.apply((FactoryChainEvaluator)evaluator);
	}

}
