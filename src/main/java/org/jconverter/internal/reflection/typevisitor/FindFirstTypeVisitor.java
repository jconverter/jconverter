package org.jconverter.internal.reflection.typevisitor;


public abstract class FindFirstTypeVisitor extends TypeFilterVisitor {

	public FindFirstTypeVisitor() {
	}
	
	public FindFirstTypeVisitor(InterfaceMode interfaceMode) {
		super(interfaceMode);
	}
	
	
	@Override
	public boolean doVisit(Class<?> clazz) {
		if(match(clazz)) {
			filteredTypes.add(clazz);
			return false; //find only one match
		}
		return true;
	}
	
	public Class<?> getFoundType() {
		if(filteredTypes.size() > 0)
			return filteredTypes.get(0);
		else
			return null;
	}
	
}
