package com.silicornio.quepotranslator;

import java.lang.reflect.ParameterizedType;

/**
 * Created by SilicorniO
 */
public abstract class QPCodeTranslation<T> {

    public abstract boolean match(T t);

    public abstract Object translate(T t);

    /**
     * Return the subtype used for this class
     */
    protected Class getSubtype(){
        return (Class<T>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
