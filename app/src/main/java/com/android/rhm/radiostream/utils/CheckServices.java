package com.android.rhm.radiostream.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by soklundy on 5/6/2017.
 */

public class CheckServices {

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
