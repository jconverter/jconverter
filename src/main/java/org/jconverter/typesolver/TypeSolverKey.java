package org.jconverter.typesolver;


import org.jcategory.category.Key;
import org.jcategory.category.SingletonKey;
import org.jconverter.JConverter;

public class TypeSolverKey extends SingletonKey {
    private TypeSolverKey(Object id) {
        super(id);
    }

    public static TypeSolverKey typeSolverKey(Object id) {
        return new TypeSolverKey(id);
    }

    public static final Key DEFAULT_KEY = typeSolverKey(JConverter.DEFAULT_CONTEXT_ID);

}