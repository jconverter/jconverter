package org.jconverter;


import static java.util.Collections.emptyList;
import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.jcategory.category.MultiKey;
import org.jconverter.converter.ConverterKey;
import org.jconverter.converter.ConverterManager;
import org.jconverter.converter.InterTypeConverterManager;
import org.jconverter.converter.TypeDomain;
import org.jconverter.factory.FactoryKey;
import org.jconverter.factory.FactoryManager;
import org.jconverter.factory.FactoryManagerImpl;
import org.jconverter.factory.SingletonFactory;
import org.typeutils.typewrapper.TypeWrapper;

public class JConverterImpl implements JConverter {

    protected MultiKey getKeys(Function<Object, Key> functionKey) {
        List<Key> keys = new ArrayList<>();
        contextIds.stream().forEach(id -> keys.add(functionKey.apply(id)));
        keys.add(functionKey.apply(JConverter.DEFAULT_CONTEXT_ID));
        return new MultiKey(keys);
    }

    protected final List<Object> contextIds;
    protected final ConverterManager converterManager; //responsible of converting objects.
    protected final FactoryManager factoryManager; //responsible of instantiating objects.

    public JConverterImpl() {
        this(new JCategory());
    }

    /**
     * @param categorization a categorization context.
     */
    protected JConverterImpl(JCategory categorization) {
        this(InterTypeConverterManager.createDefault(categorization),
            FactoryManagerImpl.createDefault(categorization),
            emptyList());
    }

    /**
     * @param converterManager a converter manager responsible of converting objects.
     * @param factoryManager an instance creator manager responsible of instantiating objects.
     */
    public JConverterImpl(ConverterManager converterManager, FactoryManager factoryManager, List<Object> contextIds) {
        this.converterManager = converterManager;
        this.factoryManager = factoryManager;
        this.contextIds = contextIds;
    }

    @Override
    public ConverterManager getConverterManager() {
        return converterManager;
    }

    @Override
    public FactoryManager getFactoryManager() {
        return factoryManager;
    }

    protected Key getConversionKey() {
        return getKeys(ConverterKey::converterKey);
    }

    protected Key getFactoryKey() {
        return getKeys(FactoryKey::factoryKey);
    }

    @Override
    public <T> T convert(Object source, Type targetType) {
        return convert(source, typeDomain(targetType));
    }

    /**
     *
     * @param source the object to convert.
     * @param target the desired type.
     * @return the conversion of the source object to the desired target type.
     */
    @Override
    public <T> T convert(Object source, TypeDomain target) {
        return convert(getConversionKey(), source, target);
    }

    <T> T convert(Key key, Object source, Type targetType) {
        return convert(key, source, typeDomain(targetType));
    }

    /**
     *
     * @param key constrains the registered converters and factories that will be looked up in this operation.
     * @param source the object to convert.
     * @param target the desired conversion domain.
     * @return the conversion of the source object to the desired target type.
     */
    <T> T convert(Key key, Object source, TypeDomain target) {
        try {
            return new SingletonFactory<T>((T) source).instantiate(target.getType()); //will launch an exception if the object source is not compatible with the target type.
        } catch(RuntimeException e) {
            return converterManager.convert(key, source, target, this);
        }
    }

    /**
     *
     * @param targetType the type to instantiate.
     * @return an instance of the desired type.
     */
    @Override
    public <T> T instantiate(Type targetType) {
        return instantiate(getFactoryKey(), targetType);
    }

    /**
     *
     * @param key constrains the instance creators that will be looked up in this operation.
     * @param targetType the type to instantiate.
     * @return
     */
    <T> T instantiate(Key key, Type targetType) {
        try {
            return factoryManager.instantiate(key, targetType);
        } catch(RuntimeException e) {
            TypeWrapper targetTypeWrapper = TypeWrapper.wrap(targetType);
            Class<T> targetClass = (Class<T>) targetTypeWrapper.getRawClass();
            try {
                return targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

}
