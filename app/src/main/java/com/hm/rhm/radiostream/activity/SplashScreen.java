package com.hm.rhm.radiostream.activity;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hm.rhm.radiostream.R;

public class SplashScreen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (isTvDevice()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startAnimations();
        new Handler().postDelayed(() -> {
            Intent intent;
            if (isTvDevice()) {
                intent = new Intent(SplashScreen.this, com.hm.rhm.radiostream.activity.tvActivity.MainActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, MainActivity.class);

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            SplashScreen.this.startActivity(intent);
            finish();

        }, SPLASH_DISPLAY_LENGTH);

    }

    private boolean isTvDevice() {
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }
        return false;
    }

    private void checkFrist() {
        /*SharedPreferencesFile mPreferencesFile = new SharedPreferencesFile(this, SharedPreferencesFile.FILENAME);
        String usrName = mPreferencesFile.getStringSharedPreference(SharedPreferencesFile.USERNAME);
        String phoneNumber = mPreferencesFile.getStringSharedPreference(SharedPreferencesFile.PHONENUMBER);
        Intent intent;
        if (usrName == null && phoneNumber == null) {
            intent = new Intent(this, Login.class);
        }else {
            intent = new Intent(this, MainActivity.class);
        }*/
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView l = (ImageView) findViewById(R.id.img_splash);
        l.clearAnimation();
        l.startAnimation(anim);
    }
}
