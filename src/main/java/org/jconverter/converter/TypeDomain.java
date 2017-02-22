package org.jconverter.converter;


import java.lang.reflect.Type;

import org.typetools.typewrapper.TypeWrapper;
import org.typetools.typewrapper.VariableTypeWrapper;

public class TypeDomain implements Domain {

    private final Type type;
    private final Class<?> rawClass;

    private TypeDomain(Type sourceType) {
        this.type = sourceType;
        TypeWrapper sourceTypeWrapper = TypeWrapper.wrap(sourceType);
        if(!(sourceTypeWrapper instanceof VariableTypeWrapper)) {
            rawClass = sourceTypeWrapper.getRawClass();
        } else {
            rawClass = null;
        }
    }

    public static TypeDomain typeDomain(Type domainType) {
        return new TypeDomain(domainType);
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }

    public boolean isVariableType() {
        return rawClass == null;
    }


    @Override
    public boolean isSubsetOf(Domain domain) {
        //TODO this may be a bit inaccurate in certain cases, to improve.
        return domain instanceof TypeDomain &&
                TypeWrapper.wrap(((TypeDomain) domain).getType()).isWeakAssignableFrom(type);
    }

    @Override
    public String toString() {
        return "Type Domain (" + getType() + ")";
    }

}
