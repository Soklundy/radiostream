package com.android.rhm.radiostream.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.SeekBar;

/**
 * Created by soklundy on 5/7/2017.
 */

public class Constants {

    public static final String TITLESTATUS = "radiostream.title888888";
    public static final String CHHANELNAME = "rhm.radiostream.chhanelname888888";
    public static final String CHHANELNUM = "rhm.radiostream.chhanelnumber888888";

    public static String INTENTSHOW = "rhm888888";
    public static String INIT_ACTION = "radiostream.init888888";
    public static String PREV_ACTION = "radiostream.prev888888";
    public static String PLAY_ACTION = "radiostream.play888888";
    public static String PAUSE_PLAY_ACTION = "rhm.radiostream.stop888888";
    public static String NEXT_ACTION = "radiostream.next888888";
    public static String STARTFOREGROUND_ACTION = "radiostream.startforeground888888";
    public static String STOPFOREGROUND_ACTION = "radiostream.stopforeground888888";

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
