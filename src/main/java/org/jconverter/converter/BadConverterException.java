package org.jconverter.converter;


public class BadConverterException extends RuntimeException {

    private final Converter<?,?> converter;

    public BadConverterException(Converter converter, String message) {
        super("Bad converter: " + converter + ". " + message);
        this.converter = converter;

    }

    public Converter<?, ?> getConverter() {
        return converter;
    }
}
