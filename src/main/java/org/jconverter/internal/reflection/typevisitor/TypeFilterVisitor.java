package org.jconverter.internal.reflection.typevisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class TypeFilterVisitor extends TypeVisitor {
	protected List<Class<?>> filteredTypes;
	
	public TypeFilterVisitor() {
		filteredTypes = new ArrayList<>();
	}
	
	public TypeFilterVisitor(InterfaceMode interfaceMode) {
		super(interfaceMode);
		filteredTypes = new ArrayList<>();
	}
	
	public List<Class<?>> getFilteredTypes() {
		return filteredTypes;
	} 
	
	@Override
	public boolean doVisit(Class<?> clazz) {
		if(match(clazz))
			filteredTypes.add(clazz);
		return true;
	}
	
	public abstract boolean match(Class<?> clazz);
}
