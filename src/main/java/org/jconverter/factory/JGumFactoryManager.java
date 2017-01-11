package org.jconverter.factory;

import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import org.jconverter.internal.reflection.TypeUtil;
import org.jconverter.internal.reflection.typewrapper.TypeWrapper;
import org.jconverter.internal.reflection.typewrapper.VariableTypeWrapper;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.type.TypeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Utility class mapping types to instance creators.
 * @author sergioc
 *
 */
public class JGumFactoryManager extends FactoryManager {
	
	private final static Logger logger = LoggerFactory.getLogger(JGumFactoryManager.class);
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return an instance of JGumFactoryManager configured with default instance creators.
	 */
	public static JGumFactoryManager createDefault(JGum jgum) {
		JGumFactoryManager instantiationManager = new JGumFactoryManager(jgum);
		registerDefaults(instantiationManager);
		return instantiationManager;
	}
	
	private final JGum jgum;
	
	public JGumFactoryManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	private FactoryChain getOrCreateChain(Object key, TypeCategory<?> typeCategory) {
		Optional<FactoryChain> chainOpt = typeCategory.getLocalProperty(key);
		FactoryChain chain;
		if(chainOpt.isPresent()) {
			chain =  chainOpt.get();
		} else {
			chain = new FactoryChain();
			typeCategory.setProperty(key, chain);
		}
		return chain;
	}
	
	@Override
	public void register(Object key, Class<?> clazz) {
		if(Modifier.isAbstract(clazz.getModifiers()))
			throw new RuntimeException(clazz.getName() + " should not be abstract.");
		List<TypeCategory<?>> abstractAncestors = (List)jgum.forClass(clazz).getAbstractAncestors();
		for(TypeCategory<?> abstractAncestor : abstractAncestors) {
			getOrCreateChain(key, abstractAncestor).addFirst(new InstantiationClassFactory(clazz));
		}
	}

	@Override
	public void register(Object key, List<Class<?>> classes, Factory<?> factory) {
		for(Class<?> clazz : classes) {
			TypeCategory<?> typeCategory = jgum.forClass(clazz);
			getOrCreateChain(key, typeCategory).addFirst(factory);
		}
	}
	
	@Override
	public void register(final Object key, final Factory<?> factory) {
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
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateChain(key, sourceTypeCategory).addFirst(factory);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = jgum.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				getOrCreateChain(key, boundTypeCategory).addFirst(factory); //set the factory for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						getOrCreateChain(key, category).addFirst(factory);
				}
			});
		}
	}

	@Override
	public <T> T instantiate(Object key, Type targetType) {
		TypeCategory<?> sourceTypeCategory = jgum.forClass(TypeWrapper.wrap(targetType).getRawClass());
		FactoryChain<T> chain = getOrCreateChain(key, sourceTypeCategory);
		FactoryEvaluator<T> factoryEvaluator = new FactoryEvaluator<>(targetType);
		FactoryChainEvaluator<T> evaluator = new FactoryChainEvaluator<>(factoryEvaluator);
		return (T)chain.apply((FactoryChainEvaluator)evaluator);
	}

}
