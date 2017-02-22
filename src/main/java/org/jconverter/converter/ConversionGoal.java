package org.jconverter.converter;


public class ConversionGoal {

    private final Object sourceObject;
    private final TypeDomain target;

    private ConversionGoal(Object sourceObject, TypeDomain target) {
        this.sourceObject = sourceObject;
        this.target = target;
    }

    public static ConversionGoal conversionGoal(Object sourceObject, TypeDomain target) {
        return new ConversionGoal(sourceObject, target);
    }

    public Object getSource() {
        return sourceObject;
    }

    public TypeDomain getTarget() {
        return target;
    }
}
