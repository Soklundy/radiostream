package com.hm.rhm.radiostream.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.utils.SharedPreferencesFile;

public class SplashScreen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesFile sharedPreferencesFile = new SharedPreferencesFile(this, SharedPreferencesFile.FILENAME);
        /*if (sharedPreferencesFile.getBooleanSharedPreference(SharedPreferencesFile.FIRSTINSTALL) == false) {*/
            setContentView(R.layout.activity_splash_screen);
            startAnimations();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
            sharedPreferencesFile.putBooleanSharedPreference(SharedPreferencesFile.FIRSTINSTALL, true);
        /*}else {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SplashScreen.this.startActivity(intent);
        }*/
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
