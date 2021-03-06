package com.hm.rhm.radiostream.activity.tvActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.hm.rhm.radiostream.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutUs extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mWebView.loadUrl("file:///android_asset/about_us.html");
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        /*mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);*/
    }
}
