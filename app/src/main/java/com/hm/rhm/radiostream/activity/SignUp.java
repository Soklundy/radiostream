package com.hm.rhm.radiostream.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.utils.API;
import com.hm.rhm.radiostream.utils.LoadingDialog;
import com.hm.rhm.radiostream.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.sp_location) Spinner spLocation;
    @BindView(R.id.sp_gender) Spinner spGender;
    @BindView(R.id.txt_dob) TextView txtDob;
    @BindView(R.id.ed_phone) EditText edPhone;
    @BindView(R.id.ed_usr) EditText edUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        initSpLocation();
        initSpGender();

    }

    private void initSpLocation() {
        // Creating adapter for spinner
        ArrayAdapter<CharSequence> dataAdapter =
                ArrayAdapter.createFromResource(this, R.array.province_arrays, R.layout.item_spinner);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spLocation.setPrompt("Select your location!!!");
        spLocation.setAdapter(dataAdapter);
    }

    private void initSpGender() {
        ArrayAdapter<CharSequence> dataAdapter =
                ArrayAdapter.createFromResource(this, R.array.arr_gender, R.layout.item_spinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(dataAdapter);
    }

    @OnClick(R.id.btn_sign_up)
    public void signUp() {
        if (spGender.getSelectedItemPosition() != 0 && spLocation.getSelectedItemPosition() != 0
                && !edUsr.getText().toString().isEmpty() && !edPhone.getText().toString().isEmpty()
                && !txtDob.getText().toString().isEmpty()) {
            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.loading();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, API.REGISTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            loadingDialog.closeLoad();
                            finish();
                            startActivity(new Intent(SignUp.this, Login.class));
                            SignUp.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                        }else if (success.equals("2")){
                            loadingDialog.closeLoad();
                            loadingDialog.alertMessage(getResources().getString(R.string.register_fail));
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
                    params.put("username", edUsr.getText().toString());
                    params.put("phone", edPhone.getText().toString());
                    params.put("gender", spGender.getSelectedItem().toString());
                    params.put("date", txtDob.getText().toString());
                    params.put("location", spLocation.getSelectedItem().toString());
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }else {
            new LoadingDialog(this).alertMessage(getResources().getString(R.string.virify_sign));
        }
    }

    @OnClick(R.id.ic_close)
    public void onClose() {
        finish();
    }

    @OnClick(R.id.txt_dob)
    public void datePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, calendar.get(Calendar.YEAR)-18, calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String sDay = String.valueOf(dayOfMonth);
        String sMonth = String.valueOf(month + 1);
        if (sMonth.length() == 1) {
            sMonth = "0" + sMonth;
        }

        if (sDay.length() == 1) {
            sDay = "0" + dayOfMonth;
        }
        txtDob.setText(sDay + "-" + sMonth + "-" + year);
    }
}
