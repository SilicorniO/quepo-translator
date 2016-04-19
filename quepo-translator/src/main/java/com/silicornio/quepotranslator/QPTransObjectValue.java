package com.silicornio.quepotranslator;

/**
 * Created by SilicorniO
 */
public class QPTransObjectValue {

    /** Name of the variable **/
    protected String name;

    /** Object and variable of the destiny object **/
    protected String destiny;

    /** Reference of another object **/
    protected String reference;

    protected QPTransObjectValue(){

    }

    protected QPTransObjectValue(String valueName, String valueDestiny){
        name = valueName;
        destiny = valueDestiny;
    }

}
