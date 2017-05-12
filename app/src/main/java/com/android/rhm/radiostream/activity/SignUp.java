package com.android.rhm.radiostream.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.rhm.radiostream.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.sp_location) Spinner spLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        // Creating adapter for spinner
        ArrayAdapter<CharSequence> dataAdapter =
                ArrayAdapter.createFromResource(this, R.array.province_arrays, R.layout.item_spinner);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spLocation.setPrompt("Select your location!!!");
        spLocation.setAdapter(dataAdapter);
    }

    @OnClick(R.id.ic_close)
    public void onClose() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
