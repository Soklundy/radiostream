package com.hm.rhm.radiostream.activity;

import android.app.Application;

import com.hm.rhm.radiostream.utils.MutiLanguage;

/**
 * Created by soklundy on 5/12/2017.
 */

public class ApplicationStartUp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new MutiLanguage(this).StartUpCheckLanguage();
    }
}
