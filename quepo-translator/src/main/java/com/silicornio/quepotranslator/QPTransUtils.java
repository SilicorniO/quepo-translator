package com.silicornio.quepotranslator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPReflectionUtils;
import com.silicornio.quepotranslator.general.QPUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransUtils {

    /** Key used to read arrays when they are received at the root **/
    public static final String KEY_ROOT_ARRAY = "array";

    /**
     * Translate the objects received with the translation object given
     * @param object Object with all objects to use
     * @param avoidClasses Class[] array of classes to not translate
     * @return Map<String, Object> with inverse translation
     */
    public static Map<String, Object> convertObjectToMapInversion(Object object, Class[] avoidClasses){
        return convertObjectToMapInversion(new Object[]{object}, avoidClasses);
    }

    /**
     * Translate the objects received with the translation object given
     * @param objects Object[] with all objects to use
     * @param avoidClasses Class[] array of classes to not translate
     * @return Map<String, Object> with inverse translation
     */
    public static Map<String, Object> convertObjectToMapInversion(Object[] objects, Class[] avoidClasses){

        //generate a map of the objects received
        Map<String, Object> mapObjects = new HashMap<>();
        for(Object obj : objects){

            //check if it is an array to add it as an array
            if ((obj instanceof List)) {

                //generate the list of maps
                List list = new ArrayList();
                for (Object o : (List) obj) {
                    list.add(QPTransUtils.convertObjectToMap(o, avoidClasses));
                }

                mapObjects.put(KEY_ROOT_ARRAY, list);

            }else{
                Map<String, Object> mapObj = QPTransUtils.convertObjectToMap(obj, avoidClasses);
                mapObjects.put(obj.getClass().getName(), mapObj);
            }
        }

        //translate the map
        return mapObjects;
    }

    /**
     * Convert the object received in a Map
     * @param object Object to convert
     * @param avoidClasses Class[] array of classes to not translate
     * @return Map<String, Object> converted
     */
    public static Map<String, Object> convertObjectToMap(Object object, Class[] avoidClasses){
        return convertObjectToMap(object, avoidClasses, new ArrayList());
    }

    /**
     * Convert the object received in a Map
     * @param object Object to convert
     * @param avoidClasses Class[] array of classes to not translate
     * @param avoidObjects List<Object> list with objects converted to avoid infinite loops
     * @return Map<String, Object> converted
     */
    private static Map<String, Object> convertObjectToMap(Object object, Class[] avoidClasses, List avoidObjects){
        Map<String, Object> map = new HashMap<>();

        //set the object values into the map
        if(object!=null){

            //check if object was already converted
            for(Object obj : avoidObjects){
                if(obj==object) {
                    QPL.i("Infinite loop blocked for object: " + object.getClass().getName());
                    return map;
                }
            }

            //add the object to the list of converted objects
            avoidObjects.add(object);

            //get the name of the variables
            List<String>[] varNames = QPReflectionUtils.getVariableNames(object.getClass());

            //primitives
            for(String varName : varNames[0]){
                Object value = QPReflectionUtils.getValue(object, varName);
                if(value!=null) {
                    map.put(varName, value);
                }
            }

            //arrays
            for(String varName : varNames[1]){
                Object valueArray = QPReflectionUtils.getValue(object, varName);
                if(valueArray!=null) {

                    List list = new ArrayList();
                    if(valueArray instanceof int[]){
                        for(Integer i : (int[])valueArray) {
                            list.add(i);
                        }
                    }else if(valueArray instanceof float[]){
                        for(Float f : (float[])valueArray) {
                            list.add(f);
                        }
                    }else if(valueArray instanceof double[]){
                        for(Double d : (double[])valueArray) {
                            list.add(d);
                        }
                    }else if(valueArray instanceof String[]){
                        for(String s : (String[])valueArray) {
                            list.add(s);
                        }
                    }else{
                        try {
                            for (Object obj : (Object[]) valueArray) {
                                if(isObjectToAvoid(obj, avoidClasses)){
                                    list.add(obj);
                                }else{
                                    list.add(convertObjectToMap(obj, avoidClasses, avoidObjects));
                                }
                            }
                        }catch(ClassCastException cce){
                            QPL.e("Type of variable with name '" + varName + "' in object '" + object.getClass().getSimpleName() + "' is not supported, sorry: " + cce.toString());
                        }
                    }
                    map.put(varName, list);
                }
            }

            //objects
            for(String varName : varNames[2]){
                Object value = QPReflectionUtils.getValue(object, varName);
                if(value!=null) {
                    if(!isObjectToAvoid(value, avoidClasses)) {
                        if ((value instanceof List)) {
                            List list = new ArrayList();
                            for (Object obj : (List) value) {
                                if (objectIsPrimitive(obj)) {
                                    list.add(obj);
                                } else {
                                    list.add(convertObjectToMap(obj, avoidClasses, avoidObjects));
                                }
                            }
                            map.put(varName, list);
                        } else {
                            map.put(varName, convertObjectToMap(value, avoidClasses, avoidObjects));
                        }
                    }else{
                        map.put(varName, value);
                    }
                }
            }
        }

        return map;
    }

    /**
     * Check if the object has a class to avoid
     * @param object Object to check
     * @param avoidClasses Class[] list of classes to avoid
     * @return boolean TRUE if the class is in the list, FALSE to continue
     */
    private static boolean isObjectToAvoid(Object object, Class[] avoidClasses){
        if(object!=null && avoidClasses!=null) {
            Class objectClass = object.getClass();
            do {
                for (Class klass : avoidClasses) {
                    if (objectClass.equals(klass)) {
                        return true;
                    }
                }
                objectClass = objectClass.getSuperclass();
            }while (objectClass!=null);
        }
        //not found
        return false;
    }

    private static boolean objectIsPrimitive(Object object){
        return (object instanceof Integer) || (object instanceof Float) || (object instanceof Double) || (object instanceof String);
    }

    /**
     * Create new objects that were used to translate the map received and include them into the objects list
     * @param objectName String to set
     * @param map Map<String, Object> with data to copy
     * @param objects List<QPTransObject> list where to save the new objects generated
     */
    protected static void generateObjects(String objectName, Map<String, Object> map, List<QPTransObject> objects){

        //generate a new object
        QPTransObject object = new QPTransObject();
        object.name = objectName;

        //check if the new object to create exists in the list, then stop
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

    /**
     * Create new objects that were used to translate the map received and include them into the objects list
     * @param objectName String to set
     * @param klass Class of the object to generate
     * @param map Map<String, Object> with data to copy
     * @param objects List<QPTransObject> list where to save the new objects generated
     */
    protected static void generateObjects(String objectName, Class klass, Map<String, Object> map, List<QPTransObject> objects){

        //generate a new object
        QPTransObject object = new QPTransObject();
        object.name = objectName;

        //check if the new object to create exists in the list, then stop
        if(objects.contains(object)){
            return;
        }

        //convert klass to name with package
        String className = klass.getName();
        int lastPoint = className.lastIndexOf(".");
        if(lastPoint!=-1){
            className = className.substring(0, lastPoint) + ":" + className.substring(lastPoint+1);
        }

        //add all the values received in the map
        object.values = new QPTransObjectValue[map.size()];
        int count = 0;
        for(Map.Entry<String, Object> entry : map.entrySet()){
            object.values[count] = new QPTransObjectValue(entry.getKey(), className + "." + entry.getKey());

            //check if the value is another map, to create another object
            if(entry.getValue() instanceof Map){

                //get the Class of the object
                Class refKlass = QPReflectionUtils.getClass(klass, entry.getKey());
                if(refKlass!=null){
                    String refName = objectName + "_" + entry.getKey();
                    generateObjects(refName, refKlass, (Map<String, Object>)entry.getValue(), objects);
                    object.values[count].reference = refName;
                }
            }else if(entry.getValue() instanceof List){

                List list = (List) entry.getValue();
                if(list.size()>0 && (list.get(0) instanceof Map)){
                    Class refKlass = QPReflectionUtils.getGenericClass(klass, entry.getKey());
                    if(refKlass!=null) {
                        String refName = objectName + "_" + entry.getKey();
                        generateObjects(refName, refKlass, (Map<String, Object>) list.get(0), objects);
                        object.values[count].reference = refName;
                    }else{
                        QPL.w("There is a list without type that won't be translated: '" + entry.getKey() + "'");
                    }
                }
            }

            count++;
        }

        //add the object to the list
        objects.add(object);

    }

    //----- JSON -----

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param isMap InputStream with JSON values
     * @return Map<String, Object> with the values of JSON
     */
    public static Map<String, Object> convertJSONToMap(InputStream isMap){

        //convert InputStream to Map
        return convertJSONToMap(QPUtils.readInputStream(isMap));
    }

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param sMap String with JSON values
     * @return Map<String, Object> with the values of JSON
     */
    public static Map<String, Object> convertJSONToMap(String sMap){

        //convert InputStream to Map
        QPUtils.startCounter("mapCreation");
        Gson gson = new Gson();

        try {

            //get the map
            LinkedTreeMap<String, Object> linkedMap = null;
            try {
                linkedMap= gson.fromJson(sMap, LinkedTreeMap.class);
            } catch (JsonSyntaxException ise) {

                //if the JSON is an array we convert it to a map
                List<LinkedTreeMap<String, Object>> aLinkedMap = gson.fromJson(sMap, List.class);
                linkedMap = new LinkedTreeMap<>();
                linkedMap.put(KEY_ROOT_ARRAY, aLinkedMap);
            }
            QPL.i("Map creation time: " + QPUtils.endCounter("mapCreation") + " ms");

            //translate map
            return linkedMap;

        }catch(JsonSyntaxException jse){
            QPL.e("Syntaxis exception in JSON received: " + jse.toString());
        }
        return null;
    }

    /**
     * Convert map to JSON
     * @param map Map<String, Object> to convert to JSON
     * @return String JSON
     */
    public static String convertMapToJSON(Map<String, Object> map){
        return convertMapToJSON(map, false);
    }

    /**
     * Convert map to JSON
     * @param map Map<String, Object> to convert to JSON
     * @param serializeNulls boolean TRUE to serialize the values as null
     * @return String JSON
     */
    public static String convertMapToJSON(Map<String, Object> map, boolean serializeNulls){
        GsonBuilder builder = new GsonBuilder();
        if(serializeNulls){
            builder.serializeNulls();
        }
        Gson gson = builder.create();

        //check if the map contains an array that should be returned as array
        if(map.containsKey(KEY_ROOT_ARRAY)){
            return gson.toJson(map.get(KEY_ROOT_ARRAY));
        }else {
            return gson.toJson(map);
        }
    }
}
