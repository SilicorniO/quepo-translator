package com.silicornio.quepotranslatorexample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.silicornio.quepotranslator.QPCodeTranslation;
import com.silicornio.quepotranslator.QPCustomTranslation;
import com.silicornio.quepotranslator.QPTransConf;
import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.QPTransResponse;
import com.silicornio.quepotranslator.QPTransUtils;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;
import com.silicornio.quepotranslatorexample.objects.ObjectDestiny;
import com.silicornio.quepotranslatorexample.objects.ObjectOrigin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by SilicorniO
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class QuepoTranslatorTests {

    Context mMockContext;

    QPTransManager mManager1;
    QPTransManager mManager2;
    QPTransManager mManager3;
    QPTransManager mManager5;
    QPTransManager mManager6;

    InputStream mIsObjectOrigin;

    @Before
    public void setUp() {

        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");

        try {
            mManager1 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation1.conf", QPTransConf.class));
            mManager2 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation2.conf", QPTransConf.class));
            mManager3 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation3.conf", QPTransConf.class));
            mManager5 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation5.conf", QPTransConf.class));
            mManager6 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation6.conf", QPTransConf.class));

            mIsObjectOrigin = mMockContext.getResources().getAssets().open("ObjectOrigin.json");

        }catch(IOException ioe){
            QPL.e("Error loading configuration: " + ioe.toString());
        }

    }

    //----- TESTS -----

    @Test
    public void test001(){

        QPTransResponse response = mManager1.translate(QPTransUtils.convertJSONToMap(mIsObjectOrigin), "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(((ObjectOrigin)response.getObject()).getVarString(), "text");
    }

    @Test
    public void test002(){

        QPTransResponse response = mManager2.translate(QPTransUtils.convertJSONToMap(mIsObjectOrigin), "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(((ObjectDestiny)response.getObject()).getVarString(), "text");
    }

    @Test
    public void test003(){

        QPTransResponse response = mManager3.translate(QPTransUtils.convertJSONToMap(mIsObjectOrigin), "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(((ObjectDestiny)response.getObject()).getVarListObjects()[0].getVarString(), "textList");
    }

    @Test
    public void test004CustomTranslator(){

        mManager3.addCustomTranslation(new QPCustomTranslation<Calendar, Date>() {
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

        Map<String, Object> mapOrigin = new HashMap<>();
        mapOrigin.put("varCalendar", Calendar.getInstance());

        QPTransResponse response = mManager3.translate(mapOrigin, "ObjectOrigin");

        //compare object received with its identifier (title)
        assertTrue(((ObjectDestiny)response.getObject()).getVarDate()!=null);
    }

    @Test
    public void test005VirtualObject(){

        QPTransResponse response = mManager5.translate(QPTransUtils.convertJSONToMap(mIsObjectOrigin), "VirtualObject");

        //compare object received with its identifier (title)
        assertEquals(((ObjectOrigin)response.getObject("exampleVirtual")).getVarInt(), 2);
    }

    @Test
    public void test006InverseTranslate(){

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarInt(1);

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, null);
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(mapInverse.get("varInt"), 1);
    }

    @Test
    public void test007InverseTranslateNestedObject(){

        ObjectOrigin oo1 = new ObjectOrigin();
        oo1.setVarString("oo1");

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarObject(oo1);

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, null);
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginParent");

        //compare object received with its identifier (title)
        assertEquals(((Map<String, Object>)mapInverse.get("varObject")).get("varString"), "oo1");
    }

    @Test
    public void test008InverseTranslateList(){

        ObjectOrigin oo1 = new ObjectOrigin();
        oo1.setVarString("oo1");

        ObjectOrigin oo2 = new ObjectOrigin();
        oo2.setVarString("oo2");

        ObjectOrigin oo = new ObjectOrigin();

        List list = new ArrayList();
        list.add(oo1);
        list.add(oo2);
        oo.setVarListObjects(list);

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, null);
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginParent");

        //compare object received with its identifier (title)
        assertEquals(((List<Map<String, Object>>)mapInverse.get("varListObjects")).get(1).get("varString"), "oo2");
    }

    @Test
    public void test009InverseTranslateArray(){

        ObjectOrigin oo1 = new ObjectOrigin();
        oo1.setVarString("oo1");

        ObjectOrigin oo2 = new ObjectOrigin();
        oo2.setVarString("oo2");

        ObjectOrigin oo = new ObjectOrigin();

        ObjectOrigin[] aObj = new ObjectOrigin[2];
        aObj[0] = oo1;
        aObj[1] = oo2;
        oo.setVarObjectArray(aObj);

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, null);
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginParent");

        //compare object received with its identifier (title)
        assertEquals(((List<Map<String, Object>>)mapInverse.get("varObjectArray")).get(1).get("varString"), "oo2");
    }

    @Test
    public void test010InverseTranslateAvoidClass(){

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarCalendar(Calendar.getInstance());

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, new Class[]{Calendar.class});
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginList");

        //compare object received with its identifier (title)
        assertTrue(mapInverse.get("varCalendar") instanceof GregorianCalendar);
    }

    @Test
    public void test011InverseTranslateCodeTranslator(){

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarCalendar(Calendar.getInstance());

        mManager6.addCodeInverseTranslation(new QPCodeTranslation<Calendar>(){

            @Override
            public boolean match(Calendar calendar) {
                return true;
            }

            @Override
            public Object translate(Calendar calendar) {
                return calendar.getTime();
            }
        });

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, new Class[]{Calendar.class});
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginList");

        //compare object received with its identifier (title)
        assertTrue(mapInverse.get("varCalendar") instanceof Date);
    }

    @Test
    public void test012InverseTranslateVirtualObject(){

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarInt(3);

        Map<String, Object> mapVirtual = new HashMap<>();
        mapVirtual.put("exampleVirtual", QPTransUtils.convertObjectToMap(oo, null));
        Map<String, Object> mapInverse = mManager6.translateInverse(mapVirtual, "VirtualObject");

        //compare object received with its identifier (title)
        assertEquals(((Map<String, Object>)mapInverse.get("varObject")).get("varInt"), 3);
    }

    @Test
    public void test013InverseTranslateInverseFormat(){

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarInt(5);

        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, null);
        Map<String, Object> mapInverse = mManager6.translateInverse(mapObjects, "ObjectOriginList");

        //compare object received with its identifier (title)
        assertEquals(mapInverse.get("varInt"), "5");
    }

    //----- END TESTS ----
}
