package org.jconverter.instantiation;

import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.Category;
import org.jgum.category.type.TypeCategory;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

import com.google.common.base.Optional;

/**
 * Utility class mapping types to instance creators.
 * @author sergioc
 *
 */
public class JGumInstantiationManager extends InstantiationManager {
	
	private final static Logger logger = Logger.getLogger(JGumInstantiationManager.class);
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return an instance of JGumInstantiationManager configured with default instance creators.
	 */
	public static JGumInstantiationManager createDefault(JGum jgum) {
		JGumInstantiationManager instantiationManager = new JGumInstantiationManager(jgum);
		registerDefaults(instantiationManager);
		return instantiationManager;
	}
	
	private final JGum jgum;
	
	public JGumInstantiationManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(Object key, Class<?> clazz) {
		if(Modifier.isAbstract(clazz.getModifiers()))
			throw new RuntimeException(clazz.getName() + " should not be abstract.");
		List<TypeCategory<?>> abstractAncestors = (List)jgum.forClass(clazz).getAbstractAncestors();
		for(TypeCategory<?> abstractAncestor : abstractAncestors) {
			abstractAncestor.setProperty(key, new SimpleInstanceCreator(clazz));
		}
	}

	@Override
	public void register(Object key, List<Class<?>> classes, InstanceCreator<?> instanceCreator) {
		for(Class<?> clazz : classes) {
			TypeCategory<?> typeCategory = jgum.forClass(clazz);
			typeCategory.setProperty(key, instanceCreator);
		}
	}
	
	@Override
	public void register(final Object key, final InstanceCreator<?> instanceCreator) {
		Type instanceCreatorType = TypeWrapper.wrap(instanceCreator.getClass()).asType(InstanceCreator.class);
		TypeWrapper instanceCreatorTypeWrapper = TypeWrapper.wrap(instanceCreatorType);
		Type sourceType = null;
		if(instanceCreatorTypeWrapper.hasActualTypeArguments()) {
			sourceType = instanceCreatorTypeWrapper.getActualTypeArguments()[0];
		} else {
			throw new RuntimeException("Instance creator does not specify a source type.");
		}
		
		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!(sourceTypeWrapper instanceof VariableTypeWrapper)) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			sourceTypeCategory.setProperty(key, instanceCreator);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = jgum.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				boundTypeCategory.setProperty(key, instanceCreator); //set the instance creator for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						category.setProperty(key, instanceCreator);
				}
			});
		}
	}

	@Override
	public <T> T instantiate(Object key, Type targetType) {
		T instantiation = null;
		Category sourceTypeCategory = jgum.forClass(TypeWrapper.wrap(targetType).getRawClass());
		Optional<InstanceCreator<T>> instanceCreatorOpt = sourceTypeCategory.<InstanceCreator<T>>getLocalProperty(key);
		if(instanceCreatorOpt.isPresent())
			instantiation = instanceCreatorOpt.get().instantiate(targetType);
		else
			throw new RuntimeException("Impossible to instantiate type: " + targetType);
		return instantiation;
	}

}
