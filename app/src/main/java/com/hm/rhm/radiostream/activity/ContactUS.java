package com.hm.rhm.radiostream.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.utils.MutiLanguage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactUS extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        final MutiLanguage mutiLanguage = new MutiLanguage(this, this);
        String lang = mutiLanguage.getLanguageCurrent();

        if (lang.equals("km") || lang.isEmpty()) {
            mWebView.loadUrl("file:///android_asset/contact_us_kh.html");
        }else {
            mWebView.loadUrl("file:///android_asset/contact_us.html");
        }
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }
}
