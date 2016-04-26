package com.silicornio.quepotranslator;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransResponse {

    /** Number of objects in the response **/
    protected int numObjects;

    /** Object in the response if there is only one **/
    protected Object object;

    /** Array of objects in the response with the order generated in configuration **/
    protected Object[] objects;

    /** Map of objects in the response **/
    protected Map<String, Object> mapObjects;

    protected QPTransResponse(){

    }

    protected QPTransResponse(Map<String, Object> mapObjects){

        //set the number of objects and the map
        numObjects = mapObjects.size();
        this.mapObjects = mapObjects;

        if(numObjects==1){
            //set one object if there is only one
            object = mapObjects.entrySet().iterator().next().getValue();
        }else if(numObjects>1){
            //set list of objects to give compatibility to previous versions
            objects = new Object[numObjects];
            int count = 0;
            for(String key : mapObjects.keySet()){
                objects[count] = mapObjects.get(key);
                count++;
            }
        }
    }

    public int getNumObjects() {
        return numObjects;
    }

    public Object getObject() {
        return object;
    }


    /**
     * @deprecated Use the map of objects to get them. It is recomended to use the getObject(Class) and getObject(String)
     */
    @Deprecated
    public Object[] getObjects() {
        return objects;
    }

    public Map<String, Object> getMapObjects() {
        return mapObjects;
    }

    @Override
    public String toString() {
        return "QPTransResponse{" +
                "numObjects=" + numObjects +
                ", object=" + object +
                ", objects=" + Arrays.toString(objects) +
                '}';
    }

    //----- ADDITIONAL METHODS -----

    /**
     * Search in the list an object of this type
     * @param klass Class of the object we want to read in the response
     * @return Object of the type requested if it is in the list or if the object
     */
    public <T>T getObject(Class<T> klass){

        if(klass==null){
            throw new IllegalArgumentException("Class received cannot be null");
        }

        //if no objects nothing to return
        if(numObjects==0){
            return null;
        }

        Object obj = getObject(klass.getName());
        if(obj!=null && obj.getClass().equals(klass)){
            return (T)obj;
        }else{
            return null;
        }
    }

    /**
     * Search in the list an object of this type
     * NOTE: Method used to read virtual objects. Destinies starting qith ':'
     * @param className String name of the class we want to read in the response.
     * @return Object of the type requested if it is in the list or if the object
     */
    public Object getObject(String className){

        if(className==null){
            throw new IllegalArgumentException("Class received cannot be null");
        }

        //if no objects nothing to return
        if(numObjects==0){
            return null;
        }

        //check num results to check one variable or other
        return mapObjects.get(className);
    }
}
