package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPReflectionUtils;

import java.util.ArrayList;
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
    private List<QPCustomTranslation> mCustomTranslations = new ArrayList<>();

    protected QPTransExecutor(){

    }

    /**
     * Add a custom translations to the list of custom translations of this manager
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    protected void addCustomTranslation(QPCustomTranslation customTranslation){
        mCustomTranslations.add(customTranslation);
    }

    /**
     * Translate the map received with the objectName using the configuration received
     * @param map Map<String, Object> to translate
     * @param objectName String name of the object
     * @param conf QPTransConf with all objects and configuration
     * @return List<Object> of generated objects
     */
    protected List<Object> translate(Map<String, Object> map, String objectName, QPTransConf conf){

        //save the conf file
        mConf = conf;

        //convert the objectName to a object
        QPTransObject transObject = null;
        if(conf!=null){
            transObject = conf.getObject(objectName);
        }else{
            mConf = new QPTransConf();
            mConf.configuration = new QPTransConfConfiguration();
        }
        if(transObject==null || (transObject.values==null && transObject.reference==null)){

            //generate the list of objects necessaries to translate the map to the configuration
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
     * Translate the map as the object with the name received
     * @param map Map<String, Object> to translate
     * @param transObject QPTransObject to use for translation
     * @return List<Object> list of objects generated
     */
    private List<Object> translate(Map<String, Object> map, QPTransObject transObject){

        //generate the map where values will be translated first
        Map<String, Object> instanceMap = translateToMap(map, transObject);

        QPL.i("Map generated: " + instanceMap.toString());

        //return the list of generated objects
        return generateObjects(instanceMap);
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

            //get the value in the map
            Object mapValue = getMapValue(transValue.name.split("\\."), 0, map);
            if(mapValue!=null) {

                //Get the object reference if it has one
                QPTransObject transObjectRef = null;
                if (transValue.reference != null) {
                    transObjectRef = mConf.getObject(transValue.reference);
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
            }else{
                QPL.i("Value with name '" + transValue.name + "' in the object '" + transObject.name + "' was not found in the map received");
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

    /**
     * Convert the map generated with translated values to a list of instances of objects
     * @param instancesMap Map<String, Object> instances of all virtual translated objects
     * @return List<Object> list of instances of objects
     */
    private List<Object> generateObjects(Map<String, Object> instancesMap){

        //generate the list of objects
        List<Object> listObjects = new ArrayList<>();

        //translate all objects
        for(Map.Entry<String, Object> entry : instancesMap.entrySet()){
            String nameClass = entry.getKey();

            //create an instance of the class and add it to the list
            Object object = QPReflectionUtils.generateInstance(nameClass);
            if(object!=null) {
                listObjects.add(object);

                //add all sub-values
                generateSubObjects((Map<String, Object>)entry.getValue(), object);
            }
        }

        //return the list of objects
        return listObjects;
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
                        QPReflectionUtils.setValue(instanceObject, entry.getKey(), object, mCustomTranslations);
                    }
                }
                if (object != null) {
                    generateSubObjects((Map<String, Object>) entry.getValue(), object);
                }

            }else if(entry.getValue() instanceof List){

                //generate a new list of objects
                List<Object> list = new ArrayList<>();

                for(Object obj : (List)entry.getValue()){
                    if(obj instanceof Map){
                        List<Object> listObjs = generateObjects((Map<String, Object>)obj);
                        if(listObjs.size()>0){
                            list.add(listObjs.get(0));
                        }
                    }else{
                        list.add(obj);
                    }
                }

                //set the value into the instanceObject
                QPReflectionUtils.setValue(instanceObject, entry.getKey(), list, mCustomTranslations);

            }else{

                //set the value into the instanceObject
                QPReflectionUtils.setValue(instanceObject, entry.getKey(), entry.getValue(), mCustomTranslations);
            }

        }

    }

}
