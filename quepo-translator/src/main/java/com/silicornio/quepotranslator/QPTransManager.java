package com.silicornio.quepotranslator;

import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SilicorniO
 */
public class QPTransManager {

    /** Configuration loaded **/
    private QPTransConf mConf;

    /** Executor loaded **/
    private QPTransExecutor mExecutor = new QPTransExecutor();

    /** List of code translations to apply to maps **/
    private List<QPCodeTranslation> mCodeTranslations = new ArrayList<>();
    private List<QPCodeTranslation> mCodeInverseTranslations = new ArrayList<>();

    public QPTransManager(QPTransConf conf){
        mConf = conf;
    }

    //----- CONFIGURATION -----

    /**
     * Add a custom translations to the list of custom translations of this manager
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    public void addCustomTranslation(QPCustomTranslation customTranslation){
        mExecutor.addCustomTranslation(customTranslation);
    }

    /**
     * Remove a custom translations from the list
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    public void removeCustomTranslation(QPCustomTranslation customTranslation){
        mExecutor.removeCustomTranslation(customTranslation);
    }

    /**
     * Set the value of the flag to check translations before general types
     * NOTE: default is false because true can produce long execution, be sure you need it
     * @param checkTranslationsFirst boolean TRUE activated, FALSE deactivated
     */
    public void setCheckTranslationsFirst(boolean checkTranslationsFirst) {
        mExecutor.setCheckTranslationsFirst(checkTranslationsFirst);
    }

    /**
     * Add the code translation to use
     * @param codeTranslation QPCodeTranslation to add
     */
    public void addCodeTranslation(QPCodeTranslation codeTranslation){
        if(codeTranslation!=null) {
            mCodeTranslations.add(codeTranslation);
        }
    }

    /**
     * Remove the code translation given
     * @param codeTranslation QPCodeTranslation to delete
     */
    public void removeCodeTranslation(QPCodeTranslation codeTranslation){
        if(codeTranslation!=null) {
            mCodeTranslations.remove(codeTranslation);
        }
    }

    /**
     * Add the code translation to use
     * @param codeTranslation QPCodeTranslation to add
     */
    public void addCodeInverseTranslation(QPCodeTranslation codeTranslation){
        if(codeTranslation!=null) {
            mCodeInverseTranslations.add(codeTranslation);
        }
    }

    /**
     * Remove the code translation given
     * @param codeTranslation QPCodeTranslation to delete
     */
    public void removeCodeInverseTranslation(QPCodeTranslation codeTranslation){
        if(codeTranslation!=null) {
            mCodeInverseTranslations.remove(codeTranslation);
        }
    }

    //----- TRANSLATIONS -----

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param map Map<String, Object> with values
     * @param objectName String name of the object to find destinies
     * @return QPTransResponse with the translated objects
     */
    public QPTransResponse translate(Map<String, Object> map, String objectName){

        if(mConf ==null || map==null){
            QPL.e("Map or object name received is null, returning empty response");
            return new QPTransResponse();
        }

        //do code translations before translate the map with configuration
        if(mCodeTranslations.size()>0) {
            QPUtils.startCounter("codeTranslations");
            doCodeTranslations(map, mCodeTranslations);
            QPL.i("Code translations time: " + QPUtils.endCounter("codeTranslations") + " ms");
        }

        //translate and show the time used
        QPUtils.startCounter("translation");
        Map<String, Object> objects = mExecutor.translate(map, objectName, mConf);
        QPL.i("Translation time: " + QPUtils.endCounter("translation") + " ms");

        return new QPTransResponse(objects);
    }

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param map Map<String, Object> with values
     * @param klass Class of the object to convert
     * @return T with the translated objects
     */
    public <T>T translate(Map<String, Object> map, Class<T> klass){

        //if no map return null
        if(map==null){
            return null;
        }

        if(mConf ==null){
            mConf = new QPTransConf();
            QPTransConfConfiguration configuration = new QPTransConfConfiguration();
            configuration.objectsPackage = klass.getPackage().getName();
            mConf.configuration = configuration;

            //generate the list of objects necessary to translate the map to the configuration
            QPTransUtils.generateObjects(klass.getSimpleName(), klass, map, mConf.objects);
        }

        //do code translations before translate the map with configuration
        doCodeTranslations(map, mCodeTranslations);

        Map<String, Object> objects = mExecutor.translate(map, klass.getSimpleName(), mConf);
        for(String key : objects.keySet()){
            if(objects.get(key).getClass().equals(klass)) {
                return (T) objects.get(key);
            }
        }
        return null;
    }

    //----- INVERSE TRANSLATIONS -----

    /**
     * Translate a map with values of objects into a map with inverse values
     * @param map Map<String, Object> with the name of the objects and inside their values
     * @param objectName String name of the object to use
     * @return Map<String, Object> with inverse translation
     */
    public Map<String, Object> translateInverse(Map<String, Object> map, String objectName){

        Map<String, Object> mapInverse = mExecutor.translateInverse(map, objectName, mConf, false);

        //do code translations before translate the map with configuration
        if(mapInverse!=null) {
            doCodeTranslations(mapInverse, mCodeInverseTranslations);
        }

        return mapInverse;
    }

    //----- CODE TRANSLATIONS -----

    /**
     * Convert the date values of a map to Date objects, necessary for translation
     * @param map Map<String, Object> where to search values
     */
    private void doCodeTranslations(Map<String, Object> map, List<QPCodeTranslation> codeTranslations){

        //check there are code translations to execute
        if(codeTranslations.size()==0){
            return;
        }

        //get the list of code translators in an array to improve speed
        QPCodeTranslation[] codeTrans = codeTranslations.toArray(new QPCodeTranslation[codeTranslations.size()]);

        //get the classes of the code translations we want to use
        Class[] classTranslations = new Class[codeTrans.length];
        for(int i=0; i<codeTrans.length; i++){
            classTranslations[i] = codeTrans[i].getSubtype();
        }

        doCodeTranslations(map, codeTrans, classTranslations);
    }

    /**
     * Convert the date values of a map to Date objects, necessary for translation
     * @param map Map<String, Object> where to search values
     * @param codeTranslations QPCodeTranslation[] array of translation instances to check
     * @param classTranslations Class[] array of classes to check faster the match of the subtype of the codeTranslations
     */
    private void doCodeTranslations(Map<String, Object> map, QPCodeTranslation[] codeTranslations, Class[] classTranslations){

        for(Map.Entry<String, Object> entry : map.entrySet()){
            Object value = entry.getValue();
            if(value!=null) {
                if (value instanceof Map) {
                    doCodeTranslations((Map<String, Object>) entry.getValue(), codeTranslations, classTranslations);

                } else if (value instanceof List) {
                    for (Object item : (List) entry.getValue()) {
                        if (item instanceof Map) {
                            doCodeTranslations((Map<String, Object>) item, codeTranslations, classTranslations);
                        }
                    }
                } else {

                    //check if the instance is equal to the code translations we have for each one
                    for (int i = 0; i < classTranslations.length; i++) {
                        Class valueClass = value.getClass();
                        do {
                            if (classTranslations[i].equals(valueClass)) {

                                //check if the match of the translation works
                                if (codeTranslations[i].match(value)) {

                                    //do the translation
                                    entry.setValue(codeTranslations[i].translate(value));
                                }
                            }
                            valueClass = valueClass.getSuperclass();
                        }while(valueClass!=null);
                    }
                }
            }
        }

    }

}
