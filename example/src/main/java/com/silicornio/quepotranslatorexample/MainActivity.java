package com.silicornio.quepotranslatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.silicornio.quepotranslator.QPCodeTranslation;
import com.silicornio.quepotranslator.QPCustomTranslation;
import com.silicornio.quepotranslator.QPTransConf;
import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;
import com.silicornio.quepotranslatorexample.objects.ObjectOrigin;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){

        QPL.showLogs = true;

        QPTransManager mManager = new QPTransManager(QPUtils.readConfObjectFromAssets(this, "translation6.conf", QPTransConf.class));
        InputStream mIsObjectOrigin = null;
        try {
            mIsObjectOrigin = getResources().getAssets().open("objectOriginNull.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarInt(1);

        ObjectOrigin oo2 = new ObjectOrigin();
        oo2.setVarInt(2);
        oo.setVarObject(oo2);

        List<ObjectOrigin> listO = new ArrayList<>();
        listO.add(oo2);
        oo.setVarListObjects(listO);

        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(gson.toJson(oo), LinkedTreeMap.class);

        QPTransManager manager = new QPTransManager(null);
        manager.addCustomTranslation(mCustomTranslationDate);
        ObjectOrigin ooTranslated = manager.translate(map, ObjectOrigin.class);

        QPL.d("VALUE: " + ooTranslated.getVarListObjects().get(0).getVarInt());

    }

    private void fillMap(Map<String, Object> map, int level){

        if(level==10){
            map.put("test", "testing");
        }else {

            for (int i = 0; i < 2; i++) {
                Map<String, Object> mapNew = new HashMap<>();
                fillMap(mapNew, level+1);
                map.put("map" + i, mapNew);
            }
        }
    }

    private static class DateQPCodeInverseTranslation extends QPCodeTranslation<Date>{

        @Override
        public boolean match(Date date) {
            return true;
        }

        @Override
        public Object translate(Date date) {
            return dateToString(date);
        }
    }

    private static class CalendarQPCodeInverseTranslation extends QPCodeTranslation<Calendar>{

        @Override
        public boolean match(Calendar calendar) {
            return true;
        }

        @Override
        public Object translate(Calendar calendar) {
            return dateToString(calendar.getTime());
        }
    }

    private static QPCustomTranslation<Calendar, Date> mCustomTranslationDate = new QPCustomTranslation<Calendar, Date>() {
        @Override
        public Date onTranslation(Calendar c) {
            return c.getTime();
        }

        @Override
        public Calendar onTranslationInverse(Date d) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return c;
        }
    };

    private static DecimalFormat mFormatter = new DecimalFormat("#00.###");
    private static String dateToString(Date date){
        int zoneOffsetMillisecond = TimeZone.getDefault().getOffset(date.getTime());
        String sign = "+";
        if (zoneOffsetMillisecond < 0) { // negative offset
            sign = "-";
            zoneOffsetMillisecond *= -1;
        }
        int minute = (int) (zoneOffsetMillisecond % (60L * 60 * 1000));
        int hour = (zoneOffsetMillisecond / 1000 / 60 / 60);
        return "/Date(" + date.getTime() + sign + mFormatter.format(hour) + mFormatter.format(minute) + ")/";
    }

}
