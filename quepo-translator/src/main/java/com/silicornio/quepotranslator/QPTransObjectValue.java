package com.silicornio.quepotranslator;

/**
 * Created by SilicorniO
 */
public class QPTransObjectValue {

    public static final String INVERSE_FORMAT_INTEGER = "integer";
    public static final String INVERSE_FORMAT_DOUBLE = "double";
    public static final String INVERSE_FORMAT_STRING = "string";
    public static final String INVERSE_FORMAT_BOOLEAN = "boolean";

    /** Name of the variable **/
    protected String name;

    /** Object and variable of the destiny object **/
    protected String destiny;

    /** Reference of another object **/
    protected String reference;

    /** Object to get in the reference **/
    protected String referenceObject;

    /** Format to apply for inversion **/
    protected String inverseFormat;

    protected QPTransObjectValue(){

    }

    protected QPTransObjectValue(String valueName, String valueDestiny){
        name = valueName;
        destiny = valueDestiny;
    }

}
