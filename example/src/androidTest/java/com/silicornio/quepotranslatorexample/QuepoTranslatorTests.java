package com.silicornio.quepotranslatorexample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.silicornio.quepotranslator.QPCustomTranslation;
import com.silicornio.quepotranslator.QPTransConf;
import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.QPTransResponse;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;
import com.silicornio.quepotranslatorexample.objects.ObjectDestiny;
import com.silicornio.quepotranslatorexample.objects.ObjectOrigin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    InputStream mIsObjectOrigin;

    @Before
    public void setUp() {

        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");

        try {
            mManager1 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation1.conf", QPTransConf.class));
            mManager2 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation2.conf", QPTransConf.class));
            mManager3 = new QPTransManager(QPUtils.readConfObjectFromAssets(mMockContext, "translation3.conf", QPTransConf.class));

            mIsObjectOrigin = mMockContext.getResources().getAssets().open("ObjectOrigin.json");

        }catch(IOException ioe){
            QPL.e("Error loading configuration: " + ioe.toString());
        }

    }

    //----- TESTS -----

    @Test
    public void test001(){

        QPTransResponse response = mManager1.translateJSON(mIsObjectOrigin, "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(((ObjectOrigin)response.getObject()).getVarString(), "text");
    }

    @Test
    public void test002(){

        QPTransResponse response = mManager2.translateJSON(mIsObjectOrigin, "ObjectOrigin");

        //compare object received with its identifier (title)
        assertEquals(((ObjectDestiny)response.getObject()).getVarString(), "text");
    }

    @Test
    public void test003(){

        QPTransResponse response = mManager3.translateJSON(mIsObjectOrigin, "ObjectOrigin");

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

    //----- END TESTS ----
}
