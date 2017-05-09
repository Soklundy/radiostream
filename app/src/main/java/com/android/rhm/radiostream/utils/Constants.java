package com.android.rhm.radiostream.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.SeekBar;

/**
 * Created by soklundy on 5/7/2017.
 */

public class Constants {

    public static final String TITLESTATUS = "com.android.rhm.radiostream.title";
    public static final String CHHANELNAME = "com.android.rhm.radiostream.chhanelname";
    public static final String CHHANELNUM = "com.android.rhm.radiostream.chhanelnumber";

    public static String INTENTSHOW = "com.android.rhm.rhm888888";
    public static String INIT_ACTION = "com.android.rhm.radiostream.init";
    public static String PREV_ACTION = "com.android.rhm.radiostream.prev";
    public static String PLAY_ACTION = "com.android.rhm.radiostream.play";
    public static String PAUSE_PLAY_ACTION = "com.android.rhm.radiostream.stop";
    public static String NEXT_ACTION = "com.android.rhm.radiostream.next";
    public static String STARTFOREGROUND_ACTION = "com.android.rhm.radiostream.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.android.rhm.radiostream.stopforeground";

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 1;
    }

    public static Bitmap getImage(Context context, int resourceDrawableImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), resourceDrawableImage, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}
