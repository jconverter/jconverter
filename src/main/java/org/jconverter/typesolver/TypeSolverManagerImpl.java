package org.jconverter.typesolver;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jcategory.JCategory;
import org.jcategory.category.CategorizationListener;
import org.jcategory.category.Category;
import org.jcategory.category.Key;
import org.jcategory.category.type.TypeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typeutils.TypeUtils;
import org.typeutils.typewrapper.TypeWrapper;
import org.typeutils.typewrapper.VariableTypeWrapper;

//Implementation note: consider moving this class and its super class to the JConverter library (it looks like it can be generalized).
public class TypeSolverManagerImpl extends TypeSolverManager {

	private final static Logger logger = LoggerFactory.getLogger(TypeSolverManagerImpl.class);

	private final JCategory categorization;
	
	public TypeSolverManagerImpl(JCategory categorization) {
		this.categorization = categorization;
	}
	
	private TypeSolverChain getOrCreateChain(Key key, TypeCategory<?> typeCategory) {
		TypeSolverChain chain;
		List<TypeSolverChain> chains = typeCategory.getLocalProperty(key);

		if (chains.isEmpty()) {
			chain = new TypeSolverChain(new ArrayList<>());
			typeCategory.setProperty(key, chain);
		} else {
			chain = chains.get(0);
		}
		return chain;
	}
	
	@Override
	public void register(Key key, TypeSolver<?> typeSolver) {
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
			TypeCategory<?> sourceTypeCategory = categorization.forClass(sourceTypeWrapper.getRawClass());
			getOrCreateChain(key, sourceTypeCategory).addFirst(typeSolver);
		} else { //the type argument is a TypeVariable with non-empty bounds.
			VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) sourceTypeWrapper;
			List<Type> upperBoundariesTypes = asList(variableTypeWrapper.getUpperBounds());
			final List<Class<?>> upperBoundariesClasses = TypeUtils.asRawClasses(upperBoundariesTypes);
			List<TypeCategory<?>> boundTypeCategories = categorization.getTypeCategorization().findBoundedTypes(upperBoundariesClasses);
			for(TypeCategory<?> boundTypeCategory : boundTypeCategories) {
				getOrCreateChain(key, boundTypeCategory).addFirst(typeSolver); //set the type solver for all the known types that are in the boundaries.
			}
			categorization.getTypeCategorization().addCategorizationListener(new CategorizationListener<TypeCategory<?>>() { //set the type solver for future known types that are in the boundaries.
				@Override
				public void onCategorization(TypeCategory<?> category) {
					if(category.isInBoundaries(upperBoundariesClasses))
						getOrCreateChain(key, category).addFirst(typeSolver);
				}
			});
		}
	}
	
	@Override
	public Type inferType(Key key, Object object) {
		Category sourceTypeCategory = categorization.forClass(object.getClass());
		List<TypeSolverChain<?>> typeSolverChains = sourceTypeCategory.<TypeSolverChain<?>>bottomUpProperties(key);
		TypeSolverChain<?> chain = new TypeSolverChain(typeSolverChains);
		TypeSolverEvaluator<?> typeSolverEvaluator = new TypeSolverEvaluator<>(object);
		TypeSolverChainEvaluator<?> evaluator = new TypeSolverChainEvaluator<>(new TypeSolverChainEvaluator.NonRedundantTypeSolverEvaluator(typeSolverEvaluator));
		return chain.apply((TypeSolverChainEvaluator)evaluator);
	}
	
}
