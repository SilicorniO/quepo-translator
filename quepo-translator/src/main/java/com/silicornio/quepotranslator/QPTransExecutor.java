package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransExecutor {

    /** Configuration used for the execution **/
    private QPTransConf mConf;

    /** List of custom translations added **/
    private QPCustomTranslation[] mCustomTranslations = new QPCustomTranslation[0];

    /** Flag to know if the translations must be checked before general types **/
    private boolean checkTranslationsFirst = false;

    /** Flag to know if null elements should be shown in the translation **/
    private boolean mTranslateNullElements = false;

    protected QPTransExecutor(){

    }

    /**
     * Add a custom translations to the list of custom translations of this manager
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    protected void addCustomTranslation(QPCustomTranslation customTranslation){
        if(customTranslation!=null) {
            List list = new ArrayList(Arrays.asList(mCustomTranslations));
            list.add(customTranslation);
            mCustomTranslations = (QPCustomTranslation[]) list.toArray(new QPCustomTranslation[list.size()]);
        }
    }

    /**
     * Remove the custom translations in the list
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    protected void removeCustomTranslation(QPCustomTranslation customTranslation){
        if(customTranslation!=null) {
            List list = new ArrayList(Arrays.asList(mCustomTranslations));
            list.remove(customTranslation);
            mCustomTranslations = (QPCustomTranslation[]) list.toArray(new QPCustomTranslation[list.size()]);
        }
    }

    /**
     * Set the value of the flag to check translations before general types
     * NOTE: default is false because true can produce long execution, be sure you need it
     * @param checkTranslationsFirst boolean TRUE activated, FALSE deactivated
     */
    public void setCheckTranslationsFirst(boolean checkTranslationsFirst) {
        this.checkTranslationsFirst = checkTranslationsFirst;
    }

    //----- INITIAL TRANSLATION -----

    /**
     * Translate the map received with the objectName using the configuration received
     * @param map Map<String, Object> to translate
     * @param objectName String name of the object
     * @param conf QPTransConf with all objects and configuration
     * @return Map<String, Object> of generated objects
     */
    protected Map<String, Object> translate(Map<String, Object> map, String objectName, QPTransConf conf){

        //save the conf file
        mConf = conf;

        //convert the objectName to a object
        QPTransObject transObject = null;
        if(conf!=null){
            transObject = conf.getObject(objectName);
        }else{
            mConf = new QPTransConf();
        }

        //check configuration
        if(mConf.configuration==null) {
            mConf.configuration = new QPTransConfConfiguration();
        }
        if(mConf.configuration.objectsPackage==null){
            mConf.configuration.objectsPackage = "";
        }

        if(transObject==null || (transObject.values==null && transObject.reference==null)){

            //generate the list of objects necessary to translate the map to the configuration
            QPTransUtils.generateObjects(objectName, map, conf.objects);

            //generate one with the map received
            transObject = conf.getObject(objectName);

        }else if(transObject.values==null && transObject.reference!=null){

            //generate one with the map received but with the reference given
            transObject = new QPTransObject(transObject.reference, map);
        }

        //translate
        return translate(map, transObject);
    }

    /**
     * Translate a map with values of objects into a map with inverse values
     * @param map Map<String, Object> with the name of the objects and inside their values
     * @param objectName String name of the object to use
     * @param translateNullElements boolean TRUE to translate null elements into the map, FALSE to not show it
     * @return Map<String, Object> with inverse translation
     */
    protected Map<String, Object> translateInverse(Map<String, Object> map, String objectName, QPTransConf conf, boolean translateNullElements){

        //save the conf file
        mConf = conf;

        //save flags
        mTranslateNullElements = translateNullElements;

        //convert the objectName to a object
        QPTransObject transObject = null;
        if(conf!=null){
            transObject = conf.getObject(objectName);
        }else{
            mConf = new QPTransConf();
        }

        //check configuration
        if(mConf.configuration==null) {
            mConf.configuration = new QPTransConfConfiguration();
        }
        if(mConf.configuration.objectsPackage==null){
            mConf.configuration.objectsPackage = "";
        }

        if(transObject==null){
            QPL.e("Object '" + objectName + "' not found");
            return null;
        }

        //translate
        return translateInverseToMap(transObject, map, true);
    }

    //----- TRANSLATION PROCESS -----

    /**
     * Translate the map as the object with the name received
     * @param map Map<String, Object> to translate
     * @param transObject QPTransObject to use for translation
     * @return Map<String, Object> of generated objects
     */
    private Map<String, Object> translate(Map<String, Object> map, QPTransObject transObject){

        //generate the map where values will be translated first
        Map<String, Object> instanceMap = translateToMap(map, transObject);

        QPL.i("Map generated: " + instanceMap.toString());

        //return the list of generated objects
        return generateObjects(instanceMap);
    }


    //----- CONVERSION FROM MAP TO MAP -----

    /**
     * Translate the map received into another map using as reference the transObject received
     * @param transObject QPTransObject to use for translation
     * @param map Map<String, Object> where to get the values to add to the map generated
     * @param rootLevel boolean TRUE to search objects in the map with package, FALSE we are inside an object (internal use only)
     * @return Map<String, Object> generated
     */
    private Map<String, Object> translateInverseToMap(QPTransObject transObject, Map<String, Object> map, boolean rootLevel){

        //generate the map to return
        Map<String, Object> mapInverse = new HashMap<>();

        //add all the values of the objects
        for(QPTransObjectValue transValue : transObject.values){

            //check has information
            if(transValue!=null && transValue.name!=null && transValue.destiny!=null) {

                //get the value of the object to translate
                Object objectValue = null;

                if (rootLevel) {
                    //get package name to get the reference in the map
                    String packageName;
                    String valueDest = transValue.destiny;
                    String aDestiny[] = transValue.destiny.split(":");
                    if (aDestiny.length > 1) {
                        packageName = aDestiny[0];
                        valueDest = aDestiny[1];
                    } else if (transObject.valuesPackage != null) {
                        packageName = transObject.valuesPackage;
                    } else {
                        packageName = mConf.configuration.objectsPackage;
                    }

                    //get the name of the object
                    String[] aValueDest = valueDest.split("\\.");
                    aValueDest[0] = packageName + "." + aValueDest[0];

                    //check if it is a virtual object to remove point from destiny
                    if (aValueDest[0].charAt(0) == '.') {
                        aValueDest[0] = aValueDest[0].substring(1);
                    }

                    //get the value in the map
                    objectValue = getMapValue(aValueDest, 0, map);

                } else {
                    String aDestiny[] = transValue.destiny.split(":");
                    if (aDestiny.length > 1) {
                        objectValue = getMapValue(aDestiny[1].split("\\."), 1, map);
                    } else {
                        objectValue = getMapValue(aDestiny[0].split("\\."), 1, map);
                    }
                }

                //put the value into the map
                if (objectValue != null) {

                    if (objectValue instanceof Map) {

                        //get the reference of the object
                        QPTransObject transObjectRef = mConf.getObject(transValue.reference);
                        if (transObjectRef != null) {
                            transObjectRef.referenceObject = transValue.referenceObject;
                            mapInverse.put(transValue.name, translateInverseToMap(transObjectRef, (Map<String, Object>) objectValue, false));
                        }

                    } else if (objectValue instanceof List) {

                        //check if has a reference to call to inverse all
                        QPTransObject transObjectRef = mConf.getObject(transValue.reference);
                        if (transObjectRef != null) {

                            List listRef = new ArrayList();
                            for (Object obj : (List) objectValue) {
                                if (obj instanceof Map) {
                                    listRef.add(translateInverseToMap(transObjectRef, (Map<String, Object>) obj, false));
                                }
                            }
                            mapInverse.put(transValue.name, listRef);

                        } else {
                            mapInverse.put(transValue.name, objectValue);
                        }

                    } else {
                        mapInverse.put(transValue.name, formatInverseValue(objectValue, transValue.inverseFormat));
                    }
                } else {
                    //put value if we want to add null elements
                    if (mTranslateNullElements) {
                        mapInverse.put(transValue.name, null);
                    }
                }
            }else{
                QPL.e("Trying to do an inverse map of the value in object '" + transObject.name + "' that hasn't got name or destiny, or value null, check sintaxis");
            }

        }

        //return the generated map
        return mapInverse;
    }

    /**
     * Change the format of the object for inverse values
     * @param object Object with the value to format
     * @param format String format to use
     * @return Object with format if was applied, else the same object
     */
    private Object formatInverseValue(Object object, String format){
        if(format!=null){
            if(QPTransObjectValue.INVERSE_FORMAT_BOOLEAN.equalsIgnoreCase(format)){
                try {
                    return Boolean.parseBoolean(String.valueOf(object));
                }catch(NumberFormatException nfe){
                    return null;
                }
            }else if(QPTransObjectValue.INVERSE_FORMAT_INTEGER.equalsIgnoreCase(format)){
                try {
                    return Integer.parseInt(String.valueOf(object));
                }catch(NumberFormatException nfe){
                    return null;
                }
            }else if(QPTransObjectValue.INVERSE_FORMAT_DOUBLE.equalsIgnoreCase(format)){
                try {
                    return Double.parseDouble(String.valueOf(object));
                }catch(NumberFormatException nfe){
                    return null;
                }
            }else if(QPTransObjectValue.INVERSE_FORMAT_STRING.equalsIgnoreCase(format)){
                return String.valueOf(object);
            }
        }
        return object;
    }

    /**
     * Translate the map as the object with the name received
     * @param map Map<String, Object> to translate
     * @param transObject QPTransObject to use for translation
     * @return Map<String, Object> generated
     */
    private Map<String, Object> translateToMap(Map<String, Object> map, QPTransObject transObject){

        //generate the map where values will be translated first
        Map<String, Object> instanceMap = new HashMap<>();

        //translate all variables
        for(QPTransObjectValue transValue : transObject.values){

            if(transValue.name!=null && transValue.destiny!=null) {

                //get the value in the map
                Object mapValue = getMapValue(transValue.name.split("\\."), 0, map);
                if (mapValue != null) {

                    //Get the object reference if it has one
                    QPTransObject transObjectRef = null;
                    if (transValue.reference != null) {
                        transObjectRef = mConf.getObject(transValue.reference);

                        //set the object reference if it has one
                        if(transValue.referenceObject!=null) {
                            String[] aReferenceObject = transValue.referenceObject.split(":");
                            if(aReferenceObject.length>1){
                                transObjectRef.referenceObject = transValue.referenceObject.replace(":", ".");
                            }else if (transObject.valuesPackage != null) {
                                transObjectRef.referenceObject = transObject.valuesPackage + "." + transValue.referenceObject;
                            }else{
                                transObjectRef.referenceObject = mConf.configuration.objectsPackage + "." + transValue.referenceObject;
                            }
                        }
                    }

                    //prepare package
                    String valuePackage;
                    String transValueDestiny = transValue.destiny;
                    String[] aValuePackage = transValueDestiny.split(":");
                    if (aValuePackage.length > 1) {
                        valuePackage = aValuePackage[0];
                        transValueDestiny = aValuePackage[1];
                    } else if (transObject.valuesPackage != null) {
                        valuePackage = transObject.valuesPackage;
                    } else {
                        valuePackage = mConf.configuration.objectsPackage;
                    }

                    //prepare values to read with the package
                    String[] aDestiny = transValueDestiny.split("\\.");
                    aDestiny[0] = (valuePackage != null ? valuePackage + "." : "") + aDestiny[0];

                    //get the object where to translate the value
                    setMapObjectDestiny(aDestiny, 0, mapValue, instanceMap, transObjectRef);
                } else {
                    QPL.i("Value with name '" + transValue.name + "' in the object '" + transObject.name + "' was not found in the map received");
                }
            }else{
                QPL.i("Ignoring value with name '\" + transValue.name + \"' in the object '\" + transObject.name + \"' because the name or the destiny were not setted");
            }
        }

        //check if this object has a reference to apply
        if(transObject.referenceObject!=null){

            //remove all maps that are not the reference
            for(String key: new ArrayList<>(instanceMap.keySet())){
                if(!transObject.referenceObject.equalsIgnoreCase(key)){
                    instanceMap.remove(key);
                }
            }
        }

        //return the list of generated objects
        return instanceMap;
    }

    /**
     * Get a value in a map from the name given with . separators
     * @param aName String[] names to access in the map to the value
     * @parma indexName int index of the name to read in the aName
     * @param map Map<String, Object> with all values
     * @return Object with the value or null if not found
     */
    private static Object getMapValue(String[] aName, int indexName, Map<String, Object> map){

        Object value = map.get(aName[indexName]);
        if(indexName==aName.length-1){
            return value;
        }else if(value instanceof Map){
            return getMapValue(aName, indexName+1, (Map<String, Object>)value);
        }else{
            return null;
        }
    }


    /**
     * Recursive method to find and generate the map of the destiny
     * @param aDestiny String[] array of text of destinies
     * @param indexDestiny int position in the array of destines to look
     * @param value Object with the value to set at the end of the destiny
     * @param instanceMap Map<String,Object> instanceObject where to check
     * @return Map<String, Object> instance final
     */
    private void setMapObjectDestiny(String[] aDestiny, int indexDestiny, Object value, Map<String,Object> instanceMap, QPTransObject transObjectRef){

        //check if it is the last index of destinies because that means the object received is final
        if(indexDestiny == aDestiny.length-1){

            //check if it is a list
            if(value instanceof List) {
                List listValue = (List)value;
                if(listValue.size()>0 && (listValue.get(0) instanceof Map)){
                    if(transObjectRef!=null) {
                        List<Map<String, Object>> listMaps = new ArrayList<>();
                        for (Map<String, Object> mapListValue : (List<Map<String, Object>>) listValue) {
                            Map<String, Object> instanceListMap = translateToMap(mapListValue, transObjectRef);
                            listMaps.add(instanceListMap);
                        }
                        instanceMap.put(aDestiny[indexDestiny], listMaps);
                    }else{
                        QPL.e("For translating nested objects in lists it is necessary to set the object reference with attribute 'reference'");
                    }
                    return;
                }
            }else if(value instanceof Map){
                //if the final destiny is an object, check if we have a reference of another object to translate
                if(transObjectRef!=null){

                    //translate the value with the object of the reference and save it as the value
                    value = translateToMap((Map<String, Object>)value, transObjectRef);
                }
            }

            instanceMap.put(aDestiny[indexDestiny], value);
            return;
        }

        //get the next instance of the object
        Object instanceSubMap = instanceMap.get(aDestiny[indexDestiny]);
        if(instanceSubMap!=null){
            if(!(instanceSubMap instanceof Map)) {
                QPL.e("Some destinies are equals with different types, one is an object");
                return;
            }
        }else{

            //generate a new instance
            instanceSubMap = new HashMap<>();

            //add it to the instance received
            instanceMap.put(aDestiny[indexDestiny], instanceSubMap);
        }

        //return the next sub-object
        setMapObjectDestiny(aDestiny, indexDestiny+1, value, (Map<String, Object>)instanceSubMap, transObjectRef);
    }

    //----- CONVERSION OF MAP TO OBJECTS -----

    /**
     * Convert the map generated with translated values to a list of instances of objects
     * @param instancesMap Map<String, Object> instances of all virtual translated objects
     * @return List<Object> list of instances of objects
     */
    private Map<String, Object> generateObjects(Map<String, Object> instancesMap){

        //generate the list and the map of objects
        Map<String, Object> mapObjects = new HashMap<>();

        //translate all objects
        for(Map.Entry<String, Object> entry : instancesMap.entrySet()){
            String nameClass = entry.getKey();

            //check if nameClass starts with point, it means we have a virtual object
            if(nameClass.length()>0 && nameClass.charAt(0)=='.'){

                if(entry.getValue() instanceof Map) {
                    Map<String, Object> mapObjectsVirtual = generateObjects((Map<String, Object>)entry.getValue());

                    //add the object in the map and the list
                    if(mapObjectsVirtual.size()>0) {
                        mapObjects.put(entry.getKey().substring(1), mapObjectsVirtual.entrySet().iterator().next().getValue());
                    }

                }else if(entry.getValue() instanceof List){

                    //create a new map where to set all the values
                    List listVirtualObjects = new ArrayList<>();
                    for(Object obj : (List)entry.getValue()){
                        if(obj instanceof Map){
                            //generate the map of the object in the list
                            Map<String, Object> mapObjectItem = generateObjects((Map<String, Object>)obj);
                            if(mapObjectItem.size()>0){
                                listVirtualObjects.add(mapObjectItem.entrySet().iterator().next().getValue());
                            }
                        }
                    }

                    //add the object in the map and the list
                    mapObjects.put(entry.getKey().substring(1), listVirtualObjects);
                }

            }else {

                //create an instance of the class and add it to the list
                Object object = QPReflectionUtils.generateInstance(nameClass);
                if (object != null) {

                    //add the object in the map and the list
                    mapObjects.put(nameClass, object);

                    //add all sub-values
                    generateSubObjects((Map<String, Object>) entry.getValue(), object);
                }
            }
        }

        //return the list of objects
        return mapObjects;
    }

    /**
     * Generate the objects inside the instance of the object received using the map
     * @param mapObject Map<String, Object> that defines what values to set
     * @param instanceObject Object generated where to set values
     */
    private void generateSubObjects(Map<String, Object> mapObject, Object instanceObject){

        for(Map.Entry<String, Object> entry : mapObject.entrySet()){

            //if it is a map we generate the instance of the value and call to generate it sub-objects
            if(entry.getValue() instanceof Map) {

                //get the object for this variable, if exists we use it, else we create an instance
                Object object = QPReflectionUtils.getValue(instanceObject, entry.getKey());
                if (object == null) {
                    object = QPReflectionUtils.generateInstance(QPReflectionUtils.getClassValue(instanceObject, entry.getKey()));
                    if (object != null) {
                        QPReflectionUtils.setValue(instanceObject, entry.getKey(), object, mCustomTranslations, checkTranslationsFirst);
                    }
                }
                if (object != null) {
                    Object mapValue = ((Map)entry.getValue()).get(object.getClass().getName());
                    if(mapValue!=null && (mapValue instanceof Map)){
                        generateSubObjects((Map<String, Object>) mapValue, object);
                    }else{
                        QPL.i("Object of type '" + object.getClass().getName() + "' trying to be filled with another type, check configuration");
                    }
                }

            }else if(entry.getValue() instanceof List){

                //generate a new list of objects
                List<Object> list = new ArrayList<>();

                for(Object obj : (List)entry.getValue()){
                    if(obj instanceof Map){
                        Map<String, Object> mapObjs = generateObjects((Map<String, Object>)obj);
                        for(String key : mapObjs.keySet()){
                            list.add(mapObjs.get(key));
                            break;
                        }
                    }else{
                        list.add(obj);
                    }
                }

                //set the value into the instanceObject
                QPReflectionUtils.setValue(instanceObject, entry.getKey(), list, mCustomTranslations, checkTranslationsFirst);

            }else{

                //set the value into the instanceObject
                QPReflectionUtils.setValue(instanceObject, entry.getKey(), entry.getValue(), mCustomTranslations, checkTranslationsFirst);
            }

        }

    }

}
