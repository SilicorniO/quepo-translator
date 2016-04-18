package com.silicornio.quepotranslatorexample.objects;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SilicorniO
 */
public class ObjectDestiny {

    private int varInt;

    private float varFloat;

    private double varDouble;

    private String varString;

    private String[] varArray;

    private List<String> varList;

    private ObjectDestiny[] varListObjects;

    private ObjectDestiny varObject;

    @Override
    public String toString() {
        return "ObjectDestiny{" +
                "varInt=" + varInt +
                ", varFloat=" + varFloat +
                ", varDouble=" + varDouble +
                ", varString='" + varString + '\'' +
                ", varArray=" + Arrays.toString(varArray) +
                ", varList=" + varList +
                ", varListObjects=" + varListObjects +
                ", varObject=" + varObject +
                '}';
    }
}
