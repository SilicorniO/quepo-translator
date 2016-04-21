package com.silicornio.quepotranslatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silicornio.quepotranslator.QPTransConf;
import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslator.general.QPUtils;
import com.silicornio.quepotranslatorexample.objects.ObjectParent;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){

        QPL.showLogs = true;

        QPTransManager manager = new QPTransManager(QPUtils.readConfObjectFromAssets(this, "translation3.conf", QPTransConf.class));

        //-----

        Map<String, Object> mapDestiny = new HashMap<>();
        mapDestiny.put("varString", "stringOrigin");

        Map<String, Object> mapParent = new HashMap<>();
        mapParent.put("varString", "stringParent");
        mapParent.put("varDestiny", mapDestiny);

        ObjectParent objParent = manager.translate(mapParent, ObjectParent.class);
        QPL.d("ObjParent: " + objParent.toString());


    }
}
