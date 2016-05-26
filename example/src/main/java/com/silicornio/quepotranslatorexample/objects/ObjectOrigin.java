package com.silicornio.quepotranslatorexample.objects;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by SilicorniO
 */
public class ObjectOrigin {

    private boolean varBool;

    private Boolean varBoolean;

    private int varInt;

    private Integer varInteger;

    private float varFloat;

    private double varDouble;

    private String varString;

    private int[] varArray;

    private ObjectOrigin[] varObjectArray;

    private List<String> varList;

    private List<ObjectOrigin> varListObjects;

    private ObjectOrigin varObject;

    private Calendar varCalendar;

    private Date varDate;

    public boolean isVarBool() {
        return varBool;
    }

    public void setVarBool(boolean varBool) {
        this.varBool = varBool;
    }

    public Boolean getVarBoolean() {
        return varBoolean;
    }

    public void setVarBoolean(Boolean varBoolean) {
        this.varBoolean = varBoolean;
    }

    public int getVarInt() {
        return varInt;
    }

    public void setVarInt(int varInt) {
        this.varInt = varInt;
    }

    public Integer getVarInteger() {
        return varInteger;
    }

    public void setVarInteger(Integer varInteger) {
        this.varInteger = varInteger;
    }

    public float getVarFloat() {
        return varFloat;
    }

    public void setVarFloat(float varFloat) {
        this.varFloat = varFloat;
    }

    public double getVarDouble() {
        return varDouble;
    }

    public void setVarDouble(double varDouble) {
        this.varDouble = varDouble;
    }

    public String getVarString() {
        return varString;
    }

    public void setVarString(String varString) {
        this.varString = varString;
    }

    public int[] getVarArray() {
        return varArray;
    }

    public void setVarArray(int[] varArray) {
        this.varArray = varArray;
    }

    public ObjectOrigin[] getVarObjectArray() {
        return varObjectArray;
    }

    public void setVarObjectArray(ObjectOrigin[] varObjectArray) {
        this.varObjectArray = varObjectArray;
    }

    public List<String> getVarList() {
        return varList;
    }

    public void setVarList(List<String> varList) {
        this.varList = varList;
    }

    public List<ObjectOrigin> getVarListObjects() {
        return varListObjects;
    }

    public void setVarListObjects(List<ObjectOrigin> varListObjects) {
        this.varListObjects = varListObjects;
    }

    public ObjectOrigin getVarObject() {
        return varObject;
    }

    public void setVarObject(ObjectOrigin varObject) {
        this.varObject = varObject;
    }

    public Calendar getVarCalendar() {
        return varCalendar;
    }

    public void setVarCalendar(Calendar varCalendar) {
        this.varCalendar = varCalendar;
    }

    public Date getVarDate() {
        return varDate;
    }

    public void setVarDate(Date varDate) {
        this.varDate = varDate;
    }

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
                ", varCalendar=" + varCalendar +
                ", varDate=" + varDate +
                '}';
    }
}
