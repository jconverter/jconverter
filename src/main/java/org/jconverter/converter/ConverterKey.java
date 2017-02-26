package org.jconverter.converter;


import org.jcategory.category.Key;
import org.jcategory.category.SingletonKey;
import org.jconverter.JConverter;

public class ConverterKey extends SingletonKey {
    public static final Key DEFAULT_KEY = converterKey(JConverter.DEFAULT_CONTEXT_ID);

    private ConverterKey(Object id) {
        super(id);
    }

    public static ConverterKey converterKey(Object id) {
        return new ConverterKey(id);
    }
}

