package com.hm.rhm.radiostream.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.android.rhm.radiostream.R;
import com.hm.rhm.radiostream.utils.API;
import com.hm.rhm.radiostream.utils.LoadingDialog;
import com.hm.rhm.radiostream.utils.MySingleton;
import com.hm.rhm.radiostream.utils.SharedPreferencesFile;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Login extends AppCompatActivity {

    @BindView(R.id.txt_phonenumber) EditText edPhoneNumber;
    @BindView(R.id.txt_usrname) EditText edUserName;
    private boolean isConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkInternetConnection();
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
        if (isConnect == true) {
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
        }else {
            loadingDialog.alertMessage(getResources().getString(R.string.no_inter));
        }
    }

    private void checkInternetConnection() {
         ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isConnectedToInternet) {
                        isConnect = isConnectedToInternet;
                    }
                });
    }

}
