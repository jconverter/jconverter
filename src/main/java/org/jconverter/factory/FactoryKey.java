package org.jconverter.factory;


import org.jcategory.category.Key;
import org.jcategory.category.SingletonKey;
import org.jconverter.JConverter;

public class FactoryKey extends SingletonKey {
    private FactoryKey(Object id) {
        super(id);
    }

    public static FactoryKey factoryKey(Object id) {
        return new FactoryKey(id);
    }

    public static final Key DEFAULT_KEY = factoryKey(JConverter.DEFAULT_CONTEXT_ID);
}
