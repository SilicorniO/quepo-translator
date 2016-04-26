package com.silicornio.quepotranslatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silicornio.quepotranslator.QPCodeTranslation;
import com.silicornio.quepotranslator.QPTransConf;
import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.QPTransUtils;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;
import com.silicornio.quepotranslatorexample.objects.ObjectOrigin;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

        QPTransManager manager = new QPTransManager(QPUtils.readConfObjectFromAssets(this, "translation6.conf", QPTransConf.class));
        InputStream isJson = null;
        try {
            isJson = getResources().getAssets().open("ObjectOrigin.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----

//        QPTransResponse response = manager.translateJSON(isJson, "VirtualObject");
//        ObjectOrigin oo = (ObjectOrigin) response.getObject("exampleVirtual");
//        QPL.d("Origin: " + oo);

        ObjectOrigin oo1 = new ObjectOrigin();
        oo1.setVarString("oo1");

//        ObjectOrigin oo2 = new ObjectOrigin();
//        oo2.setVarString("oo2");

        ObjectOrigin oo = new ObjectOrigin();
        oo.setVarObject(oo1);
//        oo.setVarInt(1);
//        oo.setVarFloat(2);
//        oo.setVarDouble(3);
//        oo.setVarString("text4");
//        oo.setVarCalendar(Calendar.getInstance());
//        oo.setVarArray(new int[]{1,2,3});
//        oo.setVarDate(new Date(0));
//        oo.setVarList(Arrays.asList(new String[]{"t1", "t2", "t3"}));
//
//        List list = new ArrayList();
//        list.add(oo1);
//        list.add(oo2);
//        oo.setVarListObjects(list);
//
//        ObjectOrigin[] aObj = new ObjectOrigin[2];
//        aObj[0] = oo1;
//        aObj[1] = oo2;
//        oo.setVarObjectArray(aObj);
//
//        manager.addCodeInverseTranslation(new DateQPCodeInverseTranslation());
//        manager.addCodeInverseTranslation(new CalendarQPCodeInverseTranslation());
//        Map<String, Object> mapObjects = QPTransUtils.convertObjectToMapInversion(oo, new Class[]{Calendar.class, Date.class});
        Map<String, Object> mapVirtual = new HashMap<>();
        mapVirtual.put("exampleVirtual", QPTransUtils.convertObjectToMap(oo, new Class[]{Calendar.class, Date.class}));
        Map<String, Object> mapInverse = manager.translateInverse(mapVirtual, "VirtualObject");
        QPL.d("INVERSE: " + QPTransUtils.convertMapToJSON(mapInverse));
//        QPL.d("INVERSE: " + mapInverse);

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
