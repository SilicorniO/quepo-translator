package com.silicornio.quepotranslator;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.silicornio.quepotranslator.general.QPL;

import java.io.InputStream;
import java.io.InputStreamReader;
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

    public QPTransManager(){

    }

    /**
     * Read and set the configuration to use
     * @param isConf InputStream with data
     */
    public boolean loadConf(InputStream isConf){

        try {
            Gson gson = new Gson();
            mConf = gson.fromJson(new InputStreamReader(isConf), QPTransConf.class);
            return true;
        }catch(Exception e){
            QPL.e("Exception loading configuration, check the JSON format: " + e.toString());
        }

        return false;
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

}
