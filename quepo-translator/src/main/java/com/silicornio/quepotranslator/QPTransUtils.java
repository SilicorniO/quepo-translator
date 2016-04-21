package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPReflectionUtils;

import java.util.HashMap;
import java.util.List;
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

    /**
     * Create new objects used to translate the map received and include them into the objects list
     * @param objectName String to set
     * @param map Map<String, Object> with data to copy
     * @param objects List<QPTransObject> list where to save the new objects generated
     */
    protected static void generateObjects(String objectName, Map<String, Object> map, List<QPTransObject> objects){

        //generate a new object
        QPTransObject object = new QPTransObject();
        object.name = objectName;

        //check if the new object to create exists in the list to stop
        if(objects.contains(object)){
            return;
        }

        //add all the values received in the map
        object.values = new QPTransObjectValue[map.size()];
        int count = 0;
        for(Map.Entry<String, Object> entry : map.entrySet()){
            object.values[count] = new QPTransObjectValue(entry.getKey(), objectName + "." + entry.getKey());

            //check if the value is another map, to create another object
            if(entry.getValue() instanceof Map){
                String reference = objectName + "_" + entry.getKey();
                generateObjects(reference, (Map<String, Object>)entry.getValue(), objects);
                object.values[count].reference = reference;
            }

            count++;
        }

        //add the object to the list
        objects.add(object);

    }


}
