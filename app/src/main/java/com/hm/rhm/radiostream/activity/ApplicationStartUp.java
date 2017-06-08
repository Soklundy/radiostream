package com.hm.rhm.radiostream.activity;

import android.app.Application;

import com.hm.rhm.radiostream.utils.MutiLanguage;

/**
 * Created by soklundy on 5/12/2017.
 */

public class ApplicationStartUp extends Application {

    private boolean isUserPressPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        new MutiLanguage(this).StartUpCheckLanguage();
    }

    public boolean isUserPressPause() {
        return isUserPressPause;
    }

    public void setUserPressPause(boolean userPressPause) {
        isUserPressPause = userPressPause;
    }
}
