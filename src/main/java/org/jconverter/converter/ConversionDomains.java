package org.jconverter.converter;


public class ConversionDomains<T extends Domain, U extends Domain> {

    private final T source;
    private final U target;

    private ConversionDomains(T source, U target) {
        this.source = source;
        this.target = target;
    }

    public static <T extends Domain, U extends Domain> ConversionDomains<T, U> conversionTypes(T source, U target) {
        return new ConversionDomains<>(source, target);
    }

    public T getSource() {
        return source;
    }

    public U getTarget() {
        return target;
    }
}
