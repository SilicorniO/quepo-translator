package com.silicornio.quepotranslatorexample.objects;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    private Calendar varCalendar;

    private Date varDate;

    public int getVarInt() {
        return varInt;
    }

    public void setVarInt(int varInt) {
        this.varInt = varInt;
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

    public String[] getVarArray() {
        return varArray;
    }

    public void setVarArray(String[] varArray) {
        this.varArray = varArray;
    }

    public List<String> getVarList() {
        return varList;
    }

    public void setVarList(List<String> varList) {
        this.varList = varList;
    }

    public ObjectDestiny[] getVarListObjects() {
        return varListObjects;
    }

    public void setVarListObjects(ObjectDestiny[] varListObjects) {
        this.varListObjects = varListObjects;
    }

    public ObjectDestiny getVarObject() {
        return varObject;
    }

    public void setVarObject(ObjectDestiny varObject) {
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
        return "ObjectDestiny{" +
                "varInt=" + varInt +
                ", varFloat=" + varFloat +
                ", varDouble=" + varDouble +
                ", varString='" + varString + '\'' +
                ", varArray=" + Arrays.toString(varArray) +
                ", varList=" + varList +
                ", varListObjects=" + Arrays.toString(varListObjects) +
                ", varObject=" + varObject +
                ", varCalendar=" + varCalendar +
                ", varDate=" + varDate +
                '}';
    }
}
