package com.silicornio.quepotranslator;

import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransObject {

    /** Name of the object to translate **/
    protected String name;

    /** Values to translate of this object **/
    protected QPTransObjectValue[] values;

    /** Name of the object where to store this one having got the same variables **/
    protected String reference;

    /** Array of name of object to include in this one **/
    protected String[] objectsInclude;

    /** Package to use with destiny objects for this element **/
    protected String valuesPackage;

    /** Name of the object to get when the value is setted from a reference **/
    protected String referenceObject;

    protected QPTransObject(){

    }

    /**
     * Create a new instance of the object with the map received, cloning names of object and variables
     * @param objectName String to set
     * @param map Map<String, Object> with data to copy
     */
    protected QPTransObject(String objectName, Map<String, Object> map){

        //set the same name
        name = objectName;

        //add all the values received in the map
        values = new QPTransObjectValue[map.size()];
        int count = 0;
        for(Map.Entry<String, Object> entry : map.entrySet()){
            values[count] = new QPTransObjectValue(entry.getKey(), name + "." + entry.getKey());
            count++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QPTransObject that = (QPTransObject) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
