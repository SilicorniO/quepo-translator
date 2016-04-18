package com.silicornio.quepotranslator;

/**
 * Created by SilicorniO
 */
public class QPTransConf {

    protected QPTransConf(){

    }

    /** Configuration **/
    protected QPTransConfConfiguration configuration;

    /** Objects **/
    protected QPTransObject[] objects;

    //----- ADDITIONAL METHODS -----

    /**
     * Get the object with the name received
     * @param nameObject String name of the object
     * @return QPTransObject found or null if not found
     */
    public QPTransObject getObject(String nameObject){

        if(objects!=null) {
            for (QPTransObject object : objects) {
                if (object.name.equals(nameObject)) {
                    return object;
                }
            }
        }

        //not found
        return null;
    }
}
