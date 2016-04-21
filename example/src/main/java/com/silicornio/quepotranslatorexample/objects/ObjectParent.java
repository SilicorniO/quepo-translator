package com.silicornio.quepotranslatorexample.objects;

/**
 * Created by SilicorniO
 */
public class ObjectParent {

    private String varString;

    private ObjectDestiny varDestiny;

    public String getVarString() {
        return varString;
    }

    public void setVarString(String varString) {
        this.varString = varString;
    }

    public ObjectDestiny getVarDestiny() {
        return varDestiny;
    }

    public void setVarDestiny(ObjectDestiny varDestiny) {
        this.varDestiny = varDestiny;
    }

    @Override
    public String toString() {
        return "ObjectParent{" +
                "varString='" + varString + '\'' +
                ", varDestiny=" + varDestiny +
                '}';
    }
}
