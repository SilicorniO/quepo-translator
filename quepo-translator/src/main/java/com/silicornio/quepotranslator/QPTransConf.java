package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SilicorniO
 */
public class QPTransConf {

    protected QPTransConf(){

    }

    /** Configuration **/
    protected QPTransConfConfiguration configuration;

    /** Objects **/
    protected List<QPTransObject> objects = new ArrayList<>();

    //----- ADDITIONAL METHODS -----

    /**
     * Get the object with the name received
     * @param nameObject String name of the object
     * @return QPTransObject found or null if not found
     */
    public QPTransObject getObject(String nameObject){
        return getObject(nameObject, new ArrayList<String>());
    }

    /**
     * Get the object with the name received
     * @param nameObject String name of the object
     * @param objectsIncluded List<String> of names of objects included, used to block infinite loops
     * @return QPTransObject found or null if not found
     */
    public QPTransObject getObject(String nameObject, List<String> objectsIncluded){

        if(objects!=null) {
            for (QPTransObject object : objects) {
                if (object.name.equals(nameObject)) {

                    //add object to the list of included
                    objectsIncluded.add(object.name);

                    //add values from includes
                    if(object.objectsInclude!=null) {
                        List<QPTransObjectValue> listValues = new ArrayList<>(Arrays.asList(object.values));
                        for (String sInclude : object.objectsInclude) {

                            if(!objectsIncluded.contains(sInclude)) {
                                //include all the values of the objects in the included list
                                QPTransObject objInclude = getObject(sInclude, objectsIncluded);
                                if (objInclude != null) {
                                    listValues.addAll(Arrays.asList(objInclude.values));
                                }
                            }else{
                                QPL.e("Check include '" + sInclude + "' of the object '" + object.name + "' because there is an infinite reference");
                            }
                        }

                        //add the values as array
                        object.values = listValues.toArray(new QPTransObjectValue[listValues.size()]);
                    }

                    return object;
                }
            }
        }

        //not found
        return null;
    }
}
