package com.silicornio.quepotranslator.parser;

import com.silicornio.quepotranslator.general.QPL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class QPJSONParser {

    private QPJSONParser(){

    }

	/**
	 * Convert the object received as JSON String
	 * @param entity Object (Map<String, Object> or List<Map>
	 * @return String with JSON generated
     */
	public static String toString(Object entity){

		try {
			//string to return	
			String sReturn = null;
			
			//check if it is an array
			if(entity instanceof List || entity instanceof ArrayList){
				
				//generate a text with the array
				StringBuilder json = new StringBuilder();
				json.append("[");
				for(Object obj : (List<?>)entity){
					if(json.length()>1){
						json.append(",");
					}
					//parse the object
					json.append(((JSONObject)toJSON(obj)).toString());
				}
				json.append("]");
				
				//return the string generated
				sReturn = json.toString();
			}else{
				
				//parse and return as text the entity
				sReturn = ((JSONObject)toJSON(entity)).toString();
			}
			
			//delete escape
			sReturn = sReturn.replace("\\/", "/");
			sReturn = sReturn.replace("\\\\", "\\");
			
			//return text
			return sReturn;
			
		} catch (Exception e) {
			QPL.e("Exception parsing value: " + e.toString());
		}

		return null;
	}

	/**
	 * Convert JSON string received to an object
	 * @param jsonString String with JSON received
	 * @return Object instance Map<String, Object> or List<Map> or NULL if error
     */
	public static Object toMapOrList(String jsonString) {

		Object objResponse = null;

		try {
			objResponse = toMap(new JSONObject(jsonString));

		} catch (JSONException jse) {
			try{
				objResponse = toList(new JSONArray(jsonString));

			}catch(JSONException jsel){
				QPL.e("Exception converting JSON string to map: " + jsel.toString());
			}
		}

		return objResponse;
	}


	//----- PARSING JSON TO HASHMAP -----

	public static HashMap<String, Object> toMap(String jsonString) throws JSONException {
		return toMap(new JSONObject(jsonString));
	}

	public static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
		HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }
	
	private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
	
	public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }
	
	//----- PARSING HASHMAP TO JSON -----

	public static Object toJSON(Object object) throws JSONException {
        if (object instanceof HashMap) {
            JSONObject json = new JSONObject();
			HashMap<String, Object> map = (HashMap<String, Object>) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable<?>)object)) {
                json.put(toJSON(value));
            }
            return json;
        } else {
            return object;
        }
    }
	
}
