package com.silicornio.quepotranslator;

import java.util.Arrays;
import java.util.List;

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

    protected QPTransResponse(){

    }

    protected QPTransResponse(List<Object> listObjects){
        numObjects = listObjects.size();
        if(numObjects==1){
            object = listObjects.get(0);
        }else if(numObjects>1){
            objects = listObjects.toArray(new Object[listObjects.size()]);
        }
    }

    public int getNumObjects() {
        return numObjects;
    }

    public Object getObject() {
        return object;
    }

    public Object[] getObjects() {
        return objects;
    }

    @Override
    public String toString() {
        return "QPTransResponse{" +
                "numObjects=" + numObjects +
                ", object=" + object +
                ", objects=" + Arrays.toString(objects) +
                '}';
    }
}
