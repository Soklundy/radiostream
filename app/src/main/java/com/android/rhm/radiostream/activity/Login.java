package com.android.rhm.radiostream.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.utils.API;
import com.android.rhm.radiostream.utils.LoadingDialog;
import com.android.rhm.radiostream.utils.MySingleton;
import com.android.rhm.radiostream.utils.SharedPreferencesFile;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.txt_phonenumber) EditText edPhoneNumber;
    @BindView(R.id.txt_usrname) EditText edUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ic_close)
    public void imgClose() {
        finish();
    }

    @OnClick(R.id.txt_go_sign)
    public void goSignUp() {
        finish();
        startActivity(new Intent(Login.this, SignUp.class));
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.loading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        SharedPreferencesFile mPreferencesFile
                                = new SharedPreferencesFile(Login.this, SharedPreferencesFile.FILENAME);
                        mPreferencesFile.putStringSharedPreference(SharedPreferencesFile.USERNAME, edUserName.getText().toString());
                        mPreferencesFile.putStringSharedPreference(SharedPreferencesFile.PHONENUMBER, edPhoneNumber.getText().toString());
                        loadingDialog.closeLoad();
                        finish();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Login.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    }else {
                        loadingDialog.closeLoad();
                        loadingDialog.alertMessage(getResources().getString(R.string.login_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.closeLoad();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", edUserName.getText().toString());
                params.put("phone", edPhoneNumber.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
