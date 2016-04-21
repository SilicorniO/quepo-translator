package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransUtils {

    /**
     * Convert the object received in a Map
     * @param object Object to convert
     * @return Map<String, Object> converted
     */
    public static Map<String, Object> convertObjectToMap(Object object){
        Map<String, Object> map = new HashMap<>();

        //set the object values into the map

        return map;
    }

    /**
     * Convert the map received in an object of type objectClass
     * @param map Map<String, Object> with all data to convert
     * @param objectClass Class of the class to generate
     * @return Object generated with the values of the map
     */
    public static <T>Object convertMapToObject(Map<String, Object> map, Class<T> objectClass){
        Object object = QPReflectionUtils.generateInstance(objectClass.getName());



        return object;
    }



}
