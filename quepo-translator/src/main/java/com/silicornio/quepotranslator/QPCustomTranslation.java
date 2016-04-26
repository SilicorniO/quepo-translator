package com.silicornio.quepotranslator;

import java.lang.reflect.ParameterizedType;

/**
 * Created by SilicorniO
 */
public abstract class QPCustomTranslation<T, U> {

    public abstract U onTranslation(T t);
    public abstract T onTranslationInverse(U u);

    /**
     * Check if the classes received are the same
     * @param klass1 Class first
     * @param klass2 Class second
     * @return int 1 same type, -1 inverse, 0 no equal
     */
    public int sameTypes(Class klass1, Class klass2){
        Class<T> klassT = (Class<T>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        Class<U> klassU = (Class<U>)(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
        if((klass1.equals(klassT) || klassT.isAssignableFrom(klass1)) && (klass2.equals(klassU) || klassU.isAssignableFrom(klass2))){
            return 1;
        }else if((klass1.equals(klassU) || klassU.isAssignableFrom(klass1)) && (klass2.equals(klassT) || klassT.isAssignableFrom(klass2))){
            return -1;
        }else{
            return 0;
        }
    }

}
