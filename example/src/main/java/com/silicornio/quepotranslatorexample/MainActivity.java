package com.silicornio.quepotranslatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.silicornio.quepotranslator.QPTransManager;
import com.silicornio.quepotranslator.QPTransResponse;
import com.silicornio.quepotranslator.general.QPL;
import com.silicornio.quepotranslatorexample.general.L;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){

        L.showLogs = true;
        QPL.showLogs = true;

        QPTransManager manager = new QPTransManager();
        try {
            manager.loadConf(getResources().getAssets().open("translation3.conf"));
        }catch(IOException ioe){
            L.e("Error loading configuration: " + ioe.toString());
        }

        try {
            QPTransResponse response = manager.translateJSON(getResources().getAssets().open("ObjectOrigin.json"), "ObjectOrigin");
            if(response.getNumObjects()==1) {
                L.d("Response: " + response.getObject().toString());
            }else{
                L.d("Response: NONE");
            }
        }catch(IOException ioe){
            L.e("Error loading configuration: " + ioe.toString());
        }

    }
}
