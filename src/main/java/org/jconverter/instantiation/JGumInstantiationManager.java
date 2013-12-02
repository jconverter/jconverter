package org.jconverter.instantiation;

import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
	
	public static JGumInstantiationManager getDefault(JGum jgum) {
		JGumInstantiationManager instantiationManager = new JGumInstantiationManager(jgum);
		instantiationManager.register(ArrayDeque.class);
		instantiationManager.register(HashMap.class);
		instantiationManager.register(HashSet.class);
		instantiationManager.register(ArrayList.class);
		instantiationManager.register(new InstanceCreator<Calendar>() {
			@Override
			public Calendar instantiate(Type type) {
				return Calendar.getInstance(); //"Gets a calendar using the default time zone and locale. The Calendar returned is based on the current time in the default time zone with the default locale."
			}
		});
		instantiationManager.register(new InstanceCreator<DateFormat>() {
			@Override
			public DateFormat instantiate(Type type) {
				return new SimpleDateFormat(); //"a SimpleDateFormat using the default pattern and date format symbols for the default locale."
			}
		});
		instantiationManager.register(new InstanceCreator<NumberFormat>() {
			@Override
			public NumberFormat instantiate(Type type) {
				return NumberFormat.getInstance(); //"a general-purpose number format for the current default locale."
			}
		});
		return instantiationManager;
	}
	
	private final JGum jgum;
	
	public JGumInstantiationManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(Object key, Class clazz) {
		if(Modifier.isAbstract(clazz.getModifiers()))
			throw new RuntimeException(clazz.getName() + " should not be abstract.");
		List<TypeCategory<?>> abstractAncestors = jgum.forClass(clazz).getAbstractAncestors();
		for(TypeCategory<?> abstractAncestor : abstractAncestors) {
			abstractAncestor.setProperty(key, new SimpleInstanceCreator(clazz));
		}
	}

	@Override
	public void register(final Object key, final InstanceCreator instanceCreator) {
		Type instanceCreatorType = TypeWrapper.wrap(instanceCreator.getClass()).asType(InstanceCreator.class);
		TypeWrapper instanceCreatorTypeWrapper = TypeWrapper.wrap(instanceCreatorType);
		Type sourceType = null;
		if(instanceCreatorTypeWrapper.hasActualTypeArguments())
			sourceType = instanceCreatorTypeWrapper.getActualTypeArguments()[0];
		if(sourceType == null || //there are no type arguments or ...
				(sourceType instanceof TypeVariable && TypeVariable.class.cast(sourceType).getBounds().length == 0)) { // ... the type argument is a type variable with empty bounds.
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
