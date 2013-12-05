package org.jconverter.typesolver;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.jconverter.typesolver.TypeSolverEvaluator.NonRedundantTypeSolverEvaluator;
import org.jgum.JGum;
import org.jgum.category.CategorizationListener;
import org.jgum.category.Category;
import org.jgum.category.type.TypeCategory;
import org.minitoolbox.reflection.TypeUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;
import org.minitoolbox.reflection.typewrapper.VariableTypeWrapper;

import com.google.common.base.Optional;

public class JGumTypeSolverManager extends TypeSolverManager {

	private final static Logger logger = Logger.getLogger(JGumTypeSolverManager.class);
	
	/**
	 * @param jgum a JGum categorization context.
	 * @return an instance of TypeSolverManager configured with default type solvers.
	 */
	public static TypeSolverManager createDefault(JGum jgum) {
		return new JGumTypeSolverManager(jgum); //no default type solvers in the current version.
	}
	
	private final JGum jgum;
	
	public JGumTypeSolverManager(JGum jgum) {
		this.jgum = jgum;
	}
	
	@Override
	public void register(final Object key, final TypeSolver typeSolver) {
		Type typeSolverType = TypeWrapper.wrap(typeSolver.getClass()).asType(TypeSolver.class);
		TypeWrapper typeSolverTypeWrapper = TypeWrapper.wrap(typeSolverType);
		Type sourceType = null;
		if(typeSolverTypeWrapper.hasActualTypeArguments()) {
			sourceType = typeSolverTypeWrapper.getActualTypeArguments()[0];
		} else {
			logger.warn("Type solver does not specify a source type. It will be registered at the Object class.");
			sourceType = Object.class;
		}

		TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
		if(!(sourceTypeWrapper instanceof VariableTypeWrapper)) {
			TypeCategory<?> sourceTypeCategory = jgum.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateChain(key, sourceTypeCategory).addFirst(typeSolver);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtil.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = jgum.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				getOrCreateChain(key, boundTypeCategory).addFirst(typeSolver); //set the type solver for all the known types that are in the boundaries.
			}
			jgum.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						getOrCreateChain(key, category).addFirst(typeSolver);
				}
			});
		}
	}

	private TypeSolverChain getOrCreateChain(Object key, TypeCategory<?> typeCategory) {
		Optional<TypeSolverChain> chainOpt = typeCategory.getLocalProperty(key);
		TypeSolverChain chain;
		if(chainOpt.isPresent()) {
			chain =  chainOpt.get();
		} else {
			chain = new TypeSolverChain();
			typeCategory.setProperty(key, chain);
		}
		return chain;
	}
	
	@Override
	public Type getType(Object key, Object object) {
		Category sourceTypeCategory = jgum.forClass(object.getClass());
		List<TypeSolverChain> typeSolverChains = sourceTypeCategory.<TypeSolverChain>bottomUpProperties(key);
		TypeSolverChain chain = new TypeSolverChain(typeSolverChains);
		TypeSolverEvaluator evaluator = new NonRedundantTypeSolverEvaluator(object);
		return (Type) chain.apply(evaluator);
	}
	
}
