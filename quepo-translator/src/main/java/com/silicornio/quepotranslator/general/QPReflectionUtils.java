package com.silicornio.quepotranslator.general;


import com.silicornio.quepotranslator.QPCustomTranslation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class QPReflectionUtils {

    //name of types of primitives
    private static final String INTEGER_NAME = "int";
    private static final String FLOAT_NAME = "float";
    private static final String DOUBLE_NAME = "double";
    private static final String BOOLEAN_NAME = "boolean";
    private static final String STRING_NAME = "string";

    //----- INSTANCES -----

    /**
     * Generate the instance of an object with the name received
     * @param nameObject String name of the class with the package
     * @return Object instance or null if not exists
     */
    public static Object generateInstance(String nameObject){

        try{
            Class klass =  Class.forName(nameObject);
            Constructor[] constructors = klass.getConstructors();
            for(Constructor c : constructors){
                Object[] params = new Object[c.getParameterTypes().length];
                try {
                    return c.newInstance(params);
                }catch(Exception e){
                }
            }

            QPL.e("The object with name '" + nameObject + "' doesn't have a public or valid constructor");

        }catch(Exception e){
            QPL.e("Error creating instance of object '" + nameObject + "'. Check the configuration is right");
        }

        return null;
    }

    //----- GENERAL -----

    /**
     * Get the field checking super objects
     * @param object Object where to search the variable
     * @param varName String name of variable
     * @return Field of the object or null if not found
     */
    private static Field getField(Object object, String varName){

        if(object==null){
            return null;
        }

        return getField(object.getClass(), varName);
    }

    /**
     * Get the field checking super objects
     * @param objectClass Class where to search the variable
     * @param varName String name of variable
     * @return Field of the object or null if not found
     */
    private static Field getField(Class objectClass, String varName){
        Field field = null;
        do {
            try {
                field = objectClass.getDeclaredField(varName);
            }catch (NoSuchFieldException nsfe){
                objectClass = objectClass.getSuperclass();
            }
        }while(field==null && objectClass!=null);
        return field;
    }

    /**
     * Get a list of the name of variables of the class received
     * @param klass Class to get the variable names
     * @return List<String>[] of length 3 with variable names: [0]: primitives, [1]: arrays, [2]: objects
     */
    public static List<String>[] getVariableNames(Class klass){

        //array to return
        List<String>[] varNames = new List[3];
        for(int i=0; i<3; i++){
            varNames[i] = new ArrayList<>();
        }

        //add all valid fields
        do {
            Field[] fields = klass.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isTransient(field.getModifiers())) {

                    //get the type
                    Class type = field.getType();

                    if (type.isPrimitive() || (type == Integer.class) || (type == Float.class) || (type == Double.class) ||
                            (type == Boolean.class) || (type == String.class)) {
                        varNames[0].add(field.getName());
                    } else if (type.isArray()) {
                        varNames[1].add(field.getName());
                    } else {
                        varNames[2].add(field.getName());
                    }
                }
            }

            klass = klass.getSuperclass();
        }while(klass!=null);


        //return array
        return varNames;

    }

    //----- GET VALUES -----

    /**
     * Return the value of a variable in an object
     * @param object Object to read
     * @param varName String name of the variable
     * @return Object or null if not found
     */
    public static Object getValue(Object object, String varName){

        try {
            //get the field checking superclass objects
            Field field = getField(object, varName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
        return null;
    }

    /**
     * Return the name of the class of a variable into an object
     * @param object Object to read
     * @param varName String name of the variable
     * @return String name of the class
     */
    public static String getClassValue(Object object, String varName){

        try {
            //get the field checking superclass objects
            Field field = getField(object, varName);
            return field.getType().getName();
        } catch (Exception e) {
            QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
        return null;
    }

    /**
     * Return the class of a variable into an object
     * @param klass Class to read
     * @param varName String name of the variable
     * @return Class  class
     */
    public static Class getClass(Class klass, String varName){

        try {
            //get the field checking superclass objects
            Field field = getField(klass, varName);
            return field.getType();
        } catch (Exception e) {
            QPL.e("Object '" + klass.getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
        return null;
    }

    //----- SET VALUES -----

    /**
     * Set the value received into the variable of the object given
     * @param object Object to read
     * @param varName String name of the variable
     * @param value Object to set into the variable
     * @param customTranslations List<QPCustomTranslation> list of translations to try another conversion if there is an error
     * @param checkTranslationsFirst boolean TRUE for check the translations before general types
     * @return Object or null if not found
     */
    public static void setValue(Object object, String varName, Object value, QPCustomTranslation[] customTranslations, boolean checkTranslationsFirst){

        Field field = null;
        Class fieldType = null;

        try {
            //get the field checking superclass objects
            field = getField(object, varName);

            if(field==null){
                QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model");
                return;
            }

            field.setAccessible(true);
            fieldType = field.getType();

            //try to set the value with custom translations
            if(checkTranslationsFirst) {
                if(setWithCustomTranslations(object, value, field, fieldType, customTranslations)){
                    return;
                }
            }

            if (fieldType == Integer.TYPE || fieldType == Integer.class) {
                field.set(object, getInteger(value));
            } else if (fieldType == Float.TYPE || fieldType == Float.class) {
                field.set(object, getFloat(value));
            } else if (fieldType == Double.TYPE || fieldType == Double.class) {
                field.set(object, getDouble(value));
            } else if (fieldType == String.class) {
                field.set(object, value.toString());
            } else if (fieldType.isArray() && (value instanceof List)) {

                if (fieldType.getComponentType() == Integer.TYPE) {
                    field.set(object, getIntegerArray((List) value));
                } else if (fieldType.getComponentType() == Float.TYPE) {
                    field.set(object, getFloatArray((List) value));
                } else if (fieldType.getComponentType() == Double.TYPE) {
                    field.set(object, getDoubleArray((List) value));
                } else {
                    field.set(object, getObjectArray((List) value, fieldType.getComponentType()));
                }

            } else {
                field.set(object, value);
            }

        }catch (IllegalArgumentException iae) {

            //try to set the value with custom translations
            if (!checkTranslationsFirst) {
                setWithCustomTranslations(object, value, field, fieldType, customTranslations);
            }

        } catch (Exception e) {
            QPL.e("Exception setting value '" + varName + "' of object '" + object.getClass().getSimpleName() + ": " + e.toString());
        }
    }

    /**
     * Set the value with the custom translations if it is possible
     * @param object Object where to set the value
     * @param value Object value to set
     * @param field Field read of the object where to set the value
     * @param fieldClass Class of the field
     * @param customTranslations QPCustomTranslation[] array of translations to checl
     * @return boolean TRUE if translation was done FALSE if not
     */
    private static boolean setWithCustomTranslations(Object object, Object value, Field field, Class fieldClass, QPCustomTranslation[] customTranslations){
        try {

            //try to get a custom translation for this objects
            for (QPCustomTranslation customTranslation : customTranslations) {
                int result = customTranslation.sameTypes(value.getClass(), fieldClass);
                if (result == 1) {
                    field.set(object, customTranslation.onTranslation(value));
                    return true;
                } else if (result == -1) {
                    field.set(object, customTranslation.onTranslationInverse(value));
                    return true;
                }

            }
        }catch(Exception e){
            QPL.e("Exception trying to set an object of a custom translation, are you returning right object?: " + e.toString());
        }

        return false;
    }


    /**
     * Get an integer from the value received
     * @param value Object to convert
     * @return int converted
     */
    public static int getInteger(Object value){
        if(value instanceof Integer){
            return ((Integer) value).intValue();
        }else if(value instanceof Double){
            return ((Double) value).intValue();
        }else if(value instanceof Float){
            return ((Float) value).intValue();
        }else{
            try {
                return Integer.parseInt(value.toString());
            }catch(NumberFormatException nfe){
                return 0;
            }
        }
    }

    /**
     * Get a float from the value received
     * @param value Object to convert
     * @return int converted
     */
    public static float getFloat(Object value){
        if(value instanceof Integer){
            return ((Integer) value).floatValue();
        }else if(value instanceof Double){
            return ((Double) value).floatValue();
        }else if(value instanceof Float){
            return ((Float) value).floatValue();
        }else{
            try {
                return Float.parseFloat(value.toString());
            }catch(NumberFormatException nfe){
                return 0;
            }
        }
    }

    /**
     * Get a double from the value received
     * @param value Object to convert
     * @return int converted
     */
    public static double getDouble(Object value){
        if(value instanceof Integer){
            return ((Integer) value).doubleValue();
        }else if(value instanceof Double){
            return ((Double) value).doubleValue();
        }else if(value instanceof Float){
            return ((Float) value).doubleValue();
        }else{
            try {
                return Double.parseDouble(value.toString());
            }catch(NumberFormatException nfe){
                return 0;
            }
        }
    }

    /**
     * Get an integer array from a list given
     * @param list List with Objects that can be translated to int
     * @return int[]
     */
    public static int[] getIntegerArray(List list){
        int[] array = new int[list.size()];
        for(int i=0; i<array.length; i++){
            Array.set(array, i, getInteger(list.get(i)));
        }
        return array;
    }

    /**
     * Get a float array from a list given
     * @param list List with Objects that can be translated to float
     * @return float[]
     */
    public static float[] getFloatArray(List list){
        float[] array = new float[list.size()];
        for(int i=0; i<array.length; i++){
            Array.set(array, i, getFloat(list.get(i)));
        }
        return array;
    }

    /**
     * Get a double array from a list given
     * @param list List with Objects that can be translated to double
     * @return double[]
     */
    public static double[] getDoubleArray(List list){
        double[] array = new double[list.size()];
        for(int i=0; i<array.length; i++){
            Array.set(array, i, getDouble(list.get(i)));
        }
        return array;
    }

    /**
     * Get a double array from a list given
     * @param list List with Objects that can be translated to double
     * @param klass Class type of array
     * @return Object array of elements
     */
    public static Object getObjectArray(List list, Class klass){
        Object array = Array.newInstance(klass, list.size());
        for(int i=0; i<list.size(); i++){
            Array.set(array, i, list.get(i));
        }
        return array;
    }

}
