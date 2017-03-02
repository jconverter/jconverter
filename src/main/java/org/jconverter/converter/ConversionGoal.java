package org.jconverter.converter;


public class ConversionGoal {

    private final Object source;
    private final TypeDomain target;

    private ConversionGoal(Object source, TypeDomain target) {
        this.source = source;
        this.target = target;
    }

    public static ConversionGoal conversionGoal(Object sourceObject, TypeDomain target) {
        return new ConversionGoal(sourceObject, target);
    }

    public Object getSource() {
        return source;
    }

    public TypeDomain getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "Source: " + source + ". Target: " + target + ".";
    }
}
