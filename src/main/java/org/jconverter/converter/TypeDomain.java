package org.jconverter.converter;


import java.lang.reflect.Type;
import java.util.Objects;

import org.typeutils.typewrapper.TypeWrapper;
import org.typeutils.typewrapper.VariableTypeWrapper;

import com.google.common.primitives.Primitives;

/**
 * A TypeDomain hierarchy should respect the invariant that any subclass adds more constraints to the ones of its parent,
 * without relaxing the inherited ones.
 * This is needed for the correct behavior of the isSubsetOf() method.
 */
public class TypeDomain {

    protected final TypeWrapper wrappedType;

    private static TypeWrapper inboxPrimitive(Type type) {
        TypeWrapper typeWrapper = TypeWrapper.wrap(type);
        if (typeWrapper.isPrimitive()) {
            return TypeWrapper.wrap(Primitives.wrap(typeWrapper.getRawClass())); //inboxing of the target type
        } else {
            return typeWrapper;
        }
    }

    protected TypeDomain(Type sourceType) {
        wrappedType = inboxPrimitive(sourceType);
    }

    public static TypeDomain typeDomain(Type domainType) {
        return new TypeDomain(domainType);
    }

    public Type getType() {
        return wrappedType.getWrappedType();
    }

    public Class<?> getRawClass() {
        return wrappedType.getRawClass();
    }

    public boolean hasVariableType() {
        return wrappedType instanceof VariableTypeWrapper;
    }

    public boolean contains(Object object) {
        return typeDomain(object.getClass()).isSubsetOf(this);
    }

    public boolean isSubsetOf(TypeDomain typeDomain) {
        return typeDomain.wrappedType.isWeakAssignableFrom(wrappedType) &&
                TypeWrapper.wrap(typeDomain.getClass()).isWeakAssignableFrom(getClass());
    }

    public TypeDomain refine(Type type) {
        if (hasType(type)) {
            return this;
        } else {
            return new TypeDomain(type);
        }
    }

    protected boolean hasType(Type type) {
        return wrappedType.getWrappedType().equals(type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TypeDomain that = (TypeDomain) o;
        return Objects.equals(wrappedType, that.wrappedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrappedType);
    }

    @Override
    public String toString() {
        return getType().toString();
    }

}
