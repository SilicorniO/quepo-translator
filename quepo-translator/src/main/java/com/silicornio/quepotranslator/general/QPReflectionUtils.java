package com.silicornio.quepotranslator.general;


import com.silicornio.quepotranslator.QPCustomTranslation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class QPReflectionUtils {

    //name of types of primitives
    private static final String INTEGER_NAME = "int";
    private static final String FLOAT_NAME = "float";
    private static final String DOUBLE_NAME = "double";
    private static final String BOOLEAN_NAME = "boolean";
    private static final String STRING_NAME = "string";

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

    /**
     * Return the value of a variable in an object
     * @param object Object to read
     * @param varName String name of the variable
     * @return Object or null if not found
     */
    public static Object getValue(Object object, String varName){

        try {
            Field field = object.getClass().getDeclaredField(varName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
        return null;
    }

    /**
     * Return the package of a variable into an object
     * @param object Object to read
     * @param varName String name of the variable
     * @return Object or null if not found
     */
    public static String getClassValue(Object object, String varName){

        try {
            Field field = object.getClass().getDeclaredField(varName);
            return field.getDeclaringClass().getName();
        } catch (Exception e) {
            QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
        return null;
    }

    /**
     * Set the value received into the variable of the object given
     * @param object Object to read
     * @param varName String name of the variable
     * @param value Object to set into the variable
     * @param customTranslations List<QPCustomTranslation> list of translations to try another conversion if there is an error
     * @return Object or null if not found
     */
    public static void setValue(Object object, String varName, Object value, List<QPCustomTranslation> customTranslations){

        Field field = null;
        Class fieldClass = null;

        try {
            field = object.getClass().getDeclaredField(varName);
            field.setAccessible(true);

            fieldClass = field.getType();
            if (fieldClass == Integer.TYPE) {
                field.set(object, getInteger(value));
            } else if (fieldClass == Float.TYPE) {
                field.set(object, getFloat(value));
            } else if (fieldClass == Double.TYPE) {
                field.set(object, getDouble(value));
            } else if (fieldClass == String.class) {
                field.set(object, value.toString());
            } else if (fieldClass.isArray() && (value instanceof List)) {

                if (fieldClass.getComponentType() == Integer.TYPE) {
                    field.set(object, getIntegerArray((List) value));
                } else if (fieldClass.getComponentType() == Float.TYPE) {
                    field.set(object, getFloatArray((List) value));
                } else if (fieldClass.getComponentType() == Double.TYPE) {
                    field.set(object, getDoubleArray((List) value));
                } else {
                    field.set(object, getObjectArray((List) value, fieldClass.getComponentType()));
                }

            } else {
                field.set(object, value);
            }

        }catch (IllegalArgumentException iae){

            try {

                //try to get a custom translation for this objects
                for (QPCustomTranslation customTranslation : customTranslations) {
                    int result = customTranslation.sameTypes(value.getClass(), fieldClass);
                    if (result == 1) {
                        field.set(object, customTranslation.onTranslation(value));
                    } else if (result == -1) {
                        field.set(object, customTranslation.onTranslationInverse(value));
                    }

                }
            }catch(Exception e){
                QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
            }

        } catch (Exception e) {
            QPL.e("Object '" + object.getClass().getSimpleName() + "' hasn't got the field '" + varName + "' of the model: " + e.toString());
        }
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
