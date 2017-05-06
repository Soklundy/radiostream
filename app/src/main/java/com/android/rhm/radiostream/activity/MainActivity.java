package com.android.rhm.radiostream.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.services.ServiceMusic;
import com.android.rhm.radiostream.utils.CheckServices;
import com.android.rhm.radiostream.utils.LoadingDialog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;

import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener, View.OnClickListener {

    private ServiceMusic.LocalBind mLocalBind;
    private boolean isBind = false;

    @BindView(R.id.hm_tv) LinearLayout hmTv;
    @BindView(R.id.hm_radio) LinearLayout hmRadio;
    @BindView(R.id.rhm_radio) LinearLayout rhmRadio;
    @BindView(R.id.player_bar) LinearLayout playerbar;

    @BindView(R.id.txt_hm) TextView txtHm;
    @BindView(R.id.txt_rhm) TextView txtRhm;
    @BindView(R.id.txt_hm_tv) TextView txtHmTv;
    @BindView(R.id.txt_playerbar) TextView txtPlayerBar;

    @BindView(R.id.ic_rhm) ImageView icRhm;
    @BindView(R.id.ic_hm) ImageView icHm;
    @BindView(R.id.ic_hm_tv) ImageView icHmTv;
    @BindView(R.id.ic_playerbar) ImageView icPlayerBar;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ButterKnife.bind(this);

        loadingDialog = new LoadingDialog(this);

        hmRadio.setOnTouchListener(this);
        hmTv.setOnTouchListener(this);
        rhmRadio.setOnTouchListener(this);

        hmRadio.setOnClickListener(this);
        rhmRadio.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the camera action
        } else if (id == R.id.nav_lang) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (id){
                case R.id.hm_tv:
                    hmTv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    txtHmTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    icHmTv.setImageResource(R.drawable.ic_tv_white);
                    break;
                case R.id.rhm_radio:
                    rhmRadio.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    txtRhm.setTextColor(getResources().getColor(R.color.colorPrimary));
                    icRhm.setImageResource(R.drawable.ic_headphone_white);
                    break;
                case R.id.hm_radio:
                    hmRadio.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    txtHm.setTextColor(getResources().getColor(R.color.colorPrimary));
                    icHm.setImageResource(R.drawable.ic_headphone_white);
                    break;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (id){
                case R.id.hm_tv:
                    hmTv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtHmTv.setTextColor(getResources().getColor(R.color.defult_textview));
                    icHmTv.setImageResource(R.drawable.ic_tv);
                    break;
                case R.id.rhm_radio:
                    rhmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtRhm.setTextColor(getResources().getColor(R.color.defult_textview));
                    icRhm.setImageResource(R.drawable.ic_headphone_org);
                    break;
                case R.id.hm_radio:
                    hmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtHm.setTextColor(getResources().getColor(R.color.defult_textview));
                    icHm.setImageResource(R.drawable.ic_headphone_org);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String channelName = "";
        String url = "";
        switch (v.getId()) {
            case R.id.hm_radio:
                channelName = txtHm.getText().toString();
                icPlayerBar.setImageResource(R.drawable.hm);
                url = "http://111.92.240.134:89/broadwavehigh.mp3";
                break;
            case R.id.rhm_radio:
                channelName = txtRhm.getText().toString();
                icPlayerBar.setImageResource(R.drawable.rhm);
                url = "http://111.92.240.134:90/broadwavehigh.mp3";
                break;
        }
        txtPlayerBar.setText(channelName);
        playerbar.setVisibility(View.VISIBLE);
        if (playerbar.getVisibility() == View.INVISIBLE){
            playerbar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }

        loadingDialog.loading();
        if (mLocalBind == null) {
            Intent intent = new Intent(this, ServiceMusic.class);
            intent.putExtra("fm_url", url);
            bindService(intent, mServiceConnect, BIND_AUTO_CREATE);
            startService(intent);
        }else {
            mLocalBind.playFm(url);
        }
    }

    @OnClick(R.id.hm_tv)
    public void hmTvOnclick() {
        Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
        playerbar.setVisibility(View.INVISIBLE);
    }

    private ServiceConnection mServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            mLocalBind = (ServiceMusic.LocalBind) service;
            mLocalBind.setLoadListener(new ExoPlayer.EventListener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY) {
                        loadingDialog.closeLoad();
                    }
                }

                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Can't loading radio")
                            .setMessage("Please check your internet connection.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    mLocalBind.stopRadio();
                    loadingDialog.closeLoad();
                }

                @Override
                public void onPositionDiscontinuity() {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind == true) {
            unbindService(mServiceConnect);
        }
    }
}
