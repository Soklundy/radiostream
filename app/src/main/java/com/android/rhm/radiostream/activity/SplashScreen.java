package com.android.rhm.radiostream.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.utils.SharedPreferencesFile;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkFrist();
    }

    private void checkFrist() {
        SharedPreferencesFile mPreferencesFile = new SharedPreferencesFile(this, SharedPreferencesFile.FILENAME);
        String usrName = mPreferencesFile.getStringSharedPreference(SharedPreferencesFile.USERNAME);
        String phoneNumber = mPreferencesFile.getStringSharedPreference(SharedPreferencesFile.PHONENUMBER);
        Intent intent;
        if (usrName == null && phoneNumber == null) {
            intent = new Intent(this, Login.class);
        }else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
