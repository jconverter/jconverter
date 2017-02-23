package org.jconverter.converter

import spock.lang.Specification

import java.lang.reflect.Type

import static org.jconverter.converter.TypeDomain.typeDomain

class TypeDomainSpec extends Specification {

    class ChildTypeDomain extends TypeDomain {
        ChildTypeDomain(Type type) {
            super(type);
        }
    }

     def 'A TypeDomain with the same type of another TypeDomain is a subset of it'() {
        given:
        TypeDomain typeDomain1 = typeDomain(String.class)
        TypeDomain typeDomain2 = typeDomain(String.class)

        expect:
        typeDomain1.isSubsetOf(typeDomain2)
    }

    def 'A TypeDomain with the subtype of another TypeDomain is a subset of it'() {
        given:
        TypeDomain typeDomain1 = typeDomain(String.class)
        TypeDomain typeDomain2 = typeDomain(Object.class)

        expect:
        typeDomain1.isSubsetOf(typeDomain2)
    }

    def 'A TypeDomain with the supertype of another TypeDomain is not subset of it'() {
        given:
        TypeDomain typeDomain1 = typeDomain(String.class)
        TypeDomain typeDomain2 = typeDomain(Object.class)

        expect:
        !typeDomain2.isSubsetOf(typeDomain1)
    }

    def 'Descendants of TypeDomain with a wrapped type being a subtype of an ancestor wrapped type is a subset of such ancestor'() {
        given:
        TypeDomain typeDomain1 = new ChildTypeDomain(ArrayList.class)
        TypeDomain typeDomain2 = typeDomain(List.class)

        expect:
        typeDomain1.isSubsetOf(typeDomain2)
    }

    def 'Descendants of TypeDomain with a wrapped type not being a subtype of an ancestor wrapped type is not a subset of such ancestor'() {
        given:
        TypeDomain typeDomain1 = new ChildTypeDomain(List.class)
        TypeDomain typeDomain2 = typeDomain(ArrayList.class)

        expect:
        !typeDomain1.isSubsetOf(typeDomain2)
    }

    def 'A TypeDomain that is not a subclass or the same class than another TypeDomain are never subsets of it'() {
        given:
        TypeDomain typeDomain1 = typeDomain(List.class)
        TypeDomain typeDomain2 = new ChildTypeDomain(ArrayList.class)
        TypeDomain typeDomain3 = new ChildTypeDomain(Object.class)

        expect:
        !typeDomain1.isSubsetOf(typeDomain2)
        !typeDomain1.isSubsetOf(typeDomain3)
    }

}
