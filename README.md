# quepo-translator
Objects translator for Android. This library allows to convert objects from one type to another defining the conversion with a JSON file.

## Features
 * Convert JSON objet into real Objects and real objects to JSON
 * Convert Map into real Objects
 * Convert Objects into Map

##Installation

You can find the latest version of the library on jCenter repository.

### For Gradle users

In your `build.gradle` you should declare the jCenter repository into `repositories` section:
```gradle
   repositories {
       jcenter()
   }
```
Include the library as dependency:
```gradle
compile 'com.silicornio:quepo-translator:1.1.2'
```

### For Maven users
```maven
<dependency>
  <groupId>com.silicornio</groupId>
  <artifactId>quepo-translator</artifactId>
  <version>1.1.2</version>
  <type>pom</type>
</dependency>
```

##Usage

1. Create a translation configuration file:

    ```json
    {
    	"configuration": {
    		"objectsPackage": "com.silicornio.quepotranslatorexample.objects"
    	},
    	"objects": [
    		{
    			"name": "ObjectOrigin",
    			"values": [
    				{
    					"name": "varListObjects",
    					"destiny": "ObjectDestiny.varListObjects",
    					"reference" : "ObjectList",
    					"referenceObject": "ObjectOrigin"
    				},
    				{
    				    "name": "varCalendar",
    				    "destiny": "ObjectDestiny.varDate"
    				},
    				{
    				    "name": "varString",
    				    "destiny": "com.silicornio.quepotranslatorexample2.objects:ObjectDestiny2.varString2"
    				}
    			]
    		},
    		{
    			"name": "ObjectList",
    			"valuesPackage": "com.silicornio.quepotranslatorexample3.objects"
    			"values": [
    				{
    					"name" : "varString",
    					"destiny": "ObjectDestiny.varString"
    				}
    			]
    		}
    	]
    }
    ```
      
  * objectsPackage - Default package where to create the instances of the objects.
  * object.name - Name of the object. This is the name we use when we want to convert a map or another object. We have to indicate the object to use for the conversion.
  * object.valuesPackage - Package to use as default for the destinies of the object
  * values.name - Name of the variable of the origin object.
  * values.destiny - [PACKAGE]:[OBJECT].[VARIABLE] where we will save the value (destiny variable of the destiny object). If we don't want to store it in any object we can create a virtual object writting ":"[OBJECT], as if the object would have an empty package.
  * values.reference - Used to link another Object when we have a list of Objects and we want to specify the type of Object to convert. For generic types is not needed.
  * values.referenceObject - This is the translated object we want to assign to this variable or add to this list or array. It is necessary when the reference object generates more than one object. If this is not setted the first object created will be returned.

2. Create a QPTransManager instance:

  We use the configuration file when we create an instance of the manager.
  
   ```java
      QPTransManager manager = new QPTransManager(QPUtils.readConfObjectFromAssets(this, "translation.conf", QPTransConf.class));
   ```

3. Translate JSON to a map:

   Most of the cases we need to translate a JSON to different objects. Quepo-translator only works with Map, so we need to convert the JSON to a Map before starting translation.
   
   ```java
      Map<String, Object> mapOrigin = QPTransUtils.convertJSONToMap(jsonText);
   ```
   
4. Translate a map into objects:

  The translation creates another map or list of maps using the configuration file. Then, the map is converted to real objects. The response is an object containing the objects translated.
  
   ```java
      QPTransResponse response = manager.translate(mapOrigin, "ObjectOrigin");
   ```

5. Read objects:

   The translation is received in an object of type QPTransResponse that contains all the objects generated. The most important variable is the map of objects because it contains all the objects generated starting with the complete name of the class or the name of the virtual object.
   
   To do easier to get the translated objects, the response has a method called "getObject" that returns the object translated giving its Class or its virtual name.
   
   ```java
      ObjectOrigin objectOrigin = response.getObject(ObjectOrigin.class);
   ```
   
   ```java
      ObjectOrigin objectOrigin = response.getObject("objectOriginVirtual");
   ```

## Additional

1. Custom translations

  Quepo-Translator convert general types as Integer, Float, Double, String, arrays and Lists. But sometimes it is necessary to convert one special object in another one or in a specific field. For that case you can use Custom-Translator. They are created in your class and added to the manager.

  This is an example for Calendar and Date conversion:

   ```java
      manager.addCustomTranslation(new QPCustomTranslation<Calendar, Date>() {
              @Override
              public Date onTranslation(Calendar calendar) {
                  return calendar.getTime();
              }
  
              @Override
              public Calendar onTranslationInverse(Date date) {
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(date);
                  return calendar;
              }
          });
   ```
2. Code translations
   
   Quepo-Translator can convert a type of a map into another type before to be processed. For example, Gson creates all numbers as double. If we are converting a numeric value to a string variable and we don't want to show decimals we can check it and convert it to integer before translation.

   We need to create a class that extends of QPCodeTranslations and it works similar to the CustomTranslations. We need to add the instance to the list of code translations of the manager. 

   ```java
      private static class Double0QPCodeTranslation extends QPCodeTranslation<Double> {

        public Double0QPCodeTranslation(){
        }

        @Override
        public boolean match(Double d) {
            return d%1 == 0;
        }

        @Override
        public Object translate(Double d) {
            return Integer.valueOf(d.intValue());
        }
      }
   ```

3. Arrays

   When an array is received directly we have to store it in a virtual object. We can create one writting a destiny value with empty package but including ":".
   
   ```json
      {
         "name": "ArrayObjectOrigin",
         "values": [
             {
                 "name": "array",
                 "destiny": ":virtualArray",
                 "reference": "ObjectOrigin"
             }
         ]
     }
   ```

## Logs

Quepo-Translator has a lot of logs, showing all the process. You can enable it but remember to disable it in production releases.

  ```java
  QTL.showLogs = true;
  ```

## License

    Copyright 2016 SilicorniO

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    


