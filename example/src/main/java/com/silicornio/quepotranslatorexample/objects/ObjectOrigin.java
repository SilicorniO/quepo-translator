package com.silicornio.quepotranslatorexample.objects;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SilicorniO
 */
public class ObjectOrigin {

    private int varInt;

    private float varFloat;

    private double varDouble;

    private String varString;

    private int[] varArray;

    private List<String> varList;

    private List<ObjectOrigin> varListObjects;

    private ObjectOrigin varObject;

    @Override
    public String toString() {
        return "ObjectOrigin{" +
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
