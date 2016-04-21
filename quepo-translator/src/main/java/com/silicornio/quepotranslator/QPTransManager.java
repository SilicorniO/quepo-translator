package com.silicornio.quepotranslator;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.silicornio.quepotranslator.general.QPL;

import java.io.InputStream;
import java.io.InputStreamReader;
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

    public QPTransManager(QPTransConf conf){
        mConf = conf;
    }

    /**
     * Add a custom translations to the list of custom translations of this manager
     * @param customTranslation QPTransCustomTranslation to use for conversions
     */
    public void addCustomTranslation(QPCustomTranslation customTranslation){
        mExecutor.addCustomTranslation(customTranslation);
    }

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param isMap InputStream with JSON values
     * @param objectName String name of the object to find destinies
     * @return QPTransResponse with the translated objects
     */
    public QPTransResponse translateJSON(InputStream isMap, String objectName){

        //convert InputStream to Map
        Gson gson = new Gson();
        LinkedTreeMap<String, Object> linkedMap = gson.fromJson(new InputStreamReader(isMap), LinkedTreeMap.class);

        //translate map
        return translate(linkedMap, objectName);
    }

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param map Map<String, Object> with values
     * @param objectName String name of the object to find destinies
     * @return QPTransResponse with the translated objects
     */
    public QPTransResponse translate(Map<String, Object> map, String objectName){

        if(mConf ==null){
            QPL.e("Configuration was not loaded");
            return new QPTransResponse();
        }

        List<Object> objects = mExecutor.translate(map, objectName, mConf);

        return new QPTransResponse(objects);
    }

    /**
     * Translate the map received with the object name associated
     * A configuration had to be loaded before: QPTransManager.loadConf()
     * @param map Map<String, Object> with values
     * @param klass Class of the object to convert
     * @return QPTransResponse with the translated objects
     */
    public <T>T translate(Map<String, Object> map, Class<T> klass){

        if(mConf ==null){
            mConf = new QPTransConf();
            QPTransConfConfiguration configuration = new QPTransConfConfiguration();
            configuration.objectsPackage = klass.getPackage().getName();
            mConf.configuration = configuration;
        }

        List<Object> objects = mExecutor.translate(map, klass.getSimpleName(), mConf);
        if(objects!=null && objects.size()>0){
            return (T)objects.get(0);
        }else{
            return null;
        }
    }

}
