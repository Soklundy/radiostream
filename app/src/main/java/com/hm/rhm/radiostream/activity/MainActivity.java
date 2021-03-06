package com.hm.rhm.radiostream.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.material.navigation.NavigationView;
import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.services.ServiceMusic;
import com.hm.rhm.radiostream.utils.CheckServices;
import com.hm.rhm.radiostream.utils.Constants;
import com.hm.rhm.radiostream.utils.LoadingDialog;
import com.hm.rhm.radiostream.utils.MutiLanguage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener, View.OnClickListener {

    private ServiceMusic.LocalBind mLocalBind;
    private boolean isBind = false;
    private String channelName;
    private boolean isConnect;
    private AnimationDrawable animation;
    private boolean isPlayerStart;

    @BindView(R.id.hm_tv)
    LinearLayout hmTv;
    @BindView(R.id.rhm_tv)
    LinearLayout rhmTv;
    @BindView(R.id.hm_radio)
    LinearLayout hmRadio;
    @BindView(R.id.rhm_radio)
    LinearLayout rhmRadio;
    @BindView(R.id.player_bar)
    LinearLayout playerbar;

    @BindView(R.id.txt_hm)
    TextView txtHm;
    @BindView(R.id.txt_rhm_tv)
    TextView txtRhmTv;
    @BindView(R.id.txt_rhm)
    TextView txtRhm;
    @BindView(R.id.txt_hm_tv)
    TextView txtHmTv;
    @BindView(R.id.txt_playerbar)
    TextView txtPlayerBar;

    @BindView(R.id.ic_rhm)
    ImageView icRhm;
    @BindView(R.id.ic_hm)
    ImageView icHm;
    @BindView(R.id.ic_hm_tv)
    ImageView icHmTv;
    @BindView(R.id.ic_rhm_tv)
    ImageView icRhmTv;
    @BindView(R.id.ic_playerbar)
    ImageView icPlayerBar;
    @BindView(R.id.ic_audio)
    ImageView imgAudio;

    private LoadingDialog loadingDialog;

    private final BroadcastReceiver finishFromOther = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    static {
        System.loadLibrary("keys");
    }

    public native String getHmTv();

    public native String getRhmTv();

    public native String getRhmRadio();

    public native String getHmRadio();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermissions();
        }
        checkInternetConnection();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*View view = navigationView.getHeaderView(0);
        TextView textView = (TextView) view.findViewById(R.id.txt_username_header);
        textView.setText(new SharedPreferencesFile(this,
                SharedPreferencesFile.FILENAME).getStringSharedPreference(SharedPreferencesFile.USERNAME));*/

        ButterKnife.bind(this);

        loadingDialog = new LoadingDialog(this);
        registerReceiver(finishFromOther, new IntentFilter("key_close"));

        hmRadio.setOnTouchListener(this);
        hmTv.setOnTouchListener(this);
        rhmRadio.setOnTouchListener(this);
        rhmTv.setOnTouchListener(this);

        hmRadio.setOnClickListener(this);
        rhmRadio.setOnClickListener(this);
        startAnimation();
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
            startActivity(new Intent(MainActivity.this, AboutUs.class));
        } else if (id == R.id.nav_lang) {
            try {
                if (mLocalBind.bindIsPlaying()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(getResources().getString(R.string.stop_radio))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    alertDiolag(this);
                }
            } catch (NullPointerException e) {
                alertDiolag(this);
            }
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(MainActivity.this, ContactUS.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (id) {
                case R.id.hm_tv:
                    hmTv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    txtHmTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    icHmTv.setImageResource(R.drawable.ic_tv_white);
                    break;
                case R.id.rhm_tv:
                    rhmTv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    txtRhmTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    icRhmTv.setImageResource(R.drawable.ic_tv_white);
                    break;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (id) {
                case R.id.hm_tv:
                    hmTv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtHmTv.setTextColor(getResources().getColor(R.color.defult_textview));
                    icHmTv.setImageResource(R.drawable.ic_tv);
                    break;
                case R.id.rhm_tv:
                    rhmTv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtRhmTv.setTextColor(getResources().getColor(R.color.defult_textview));
                    icRhmTv.setImageResource(R.drawable.ic_tv);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()) {
            case R.id.hm_radio:
                if (mLocalBind != null) {
                    ColorDrawable colorHmRadio = (ColorDrawable) hmRadio.getBackground();
                    if (mLocalBind.bindIsPlaying() && getResources().getColor(R.color.colorAccent) == colorHmRadio.getColor()) {
                        return;
                    }
                }
                channelName = txtHm.getText().toString();
                icPlayerBar.setImageResource(R.drawable.ic_104);
                url = getHmRadio();
                hmRadio.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                txtHm.setTextColor(getResources().getColor(R.color.colorPrimary));
                icHm.setImageResource(R.drawable.ic_headphone_white);

                /*un hightlight hm*/
                rhmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtRhm.setTextColor(getResources().getColor(R.color.defult_textview));
                icRhm.setImageResource(R.drawable.ic_headphone_org);
                break;
            case R.id.rhm_radio:
                if (mLocalBind != null) {
                    ColorDrawable colorHmRadio = (ColorDrawable) rhmRadio.getBackground();
                    if (mLocalBind.bindIsPlaying() && getResources().getColor(R.color.colorAccent) == colorHmRadio.getColor()) {
                        return;
                    }
                }
                channelName = txtRhm.getText().toString();
                icPlayerBar.setImageResource(R.drawable.ic_95);
                url = getRhmRadio();
                rhmRadio.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                txtRhm.setTextColor(getResources().getColor(R.color.colorPrimary));
                icRhm.setImageResource(R.drawable.ic_headphone_white);

                /*un hightlight hm*/
                hmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtHm.setTextColor(getResources().getColor(R.color.defult_textview));
                icHm.setImageResource(R.drawable.ic_headphone_org);
                break;
        }
        if (isConnect == true) {
            hideShowStatusBar(channelName);
            loadingDialog.loading();
            if (mLocalBind == null) {
                if (new CheckServices().isMyServiceRunning(ServiceMusic.class, this)) {
                    stopService(new Intent(this, ServiceMusic.class));
                }
                startStart(url, channelName);

            } else {
                mLocalBind.playFm(url, channelName);
                Log.e("test_service", "mlocalBind!=null_work");
            }
        } else {
            loadingDialog.alert(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*rhm*/
                    rhmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtRhm.setTextColor(getResources().getColor(R.color.defult_textview));
                    icRhm.setImageResource(R.drawable.ic_headphone_org);

                    /*hm*/
                    hmRadio.setBackgroundColor(getResources().getColor(R.color.transparent));
                    txtHm.setTextColor(getResources().getColor(R.color.defult_textview));
                    icHm.setImageResource(R.drawable.ic_headphone_org);
                    rhmRadio.setEnabled(true);
                    hmRadio.setEnabled(true);
                    dialog.dismiss();
                }
            });
        }
    }

    @OnClick(R.id.hm_tv)
    public void hmTvOnclick() {
        if (isConnect == true) {
            if (mLocalBind != null) {
                mLocalBind.exoPlayerPause();
            }
            Intent intent = new Intent(this, VideoExoplayer.class);
            intent.putExtra("tv_url", getHmTv());
            intent.putExtra("txt_channel", getResources().getString(R.string.hm_tv));
            startActivity(intent);

        } else {
            loadingDialog.alert();
        }
    }

    @OnClick(R.id.rhm_tv)
    public void rHmTvOnclick() {
        if (isConnect == true) {
            if (mLocalBind != null) {
                mLocalBind.exoPlayerPause();
            }
            Intent intent = new Intent(this, VideoExoplayer.class);
            intent.putExtra("tv_url", getRhmTv());
            intent.putExtra("txt_channel", getResources().getString(R.string.rhm_tv));
            startActivity(intent);
        } else {
            loadingDialog.alert();
        }
    }

    private void hideShowStatusBar(String channelName) {
        txtPlayerBar.setText(channelName);
        playerbar.setVisibility(View.VISIBLE);
        if (playerbar.getVisibility() == View.INVISIBLE) {
            playerbar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }
    }

    private void startStart(String url, String ch) {
        Intent intent = new Intent(this, ServiceMusic.class);
        intent.putExtra("fm_url", url);
        intent.putExtra(Constants.TITLESTATUS, ch);
        intent.setAction(Constants.STARTFOREGROUND_ACTION);
        bindService(intent, mServiceConnect, BIND_AUTO_CREATE);
        startService(intent);
        Log.e("test_service", "mlocalBind=null_work");
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
                        animation.start();
                    }

                    if (playWhenReady == false) {
                        animation.stop();
                    }
                    isPlayerStart = playWhenReady;
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.can_load))
                            .setMessage(getResources().getString(R.string.check_inter))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    loadingDialog.closeLoad();
                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = true;
        }
    };

    @OnClick(R.id.player_bar)
    public void playerBarOnClick() {
        Intent intent = new Intent(this, RadioDetail.class);
        intent.putExtra(Constants.CHHANELNAME, channelName);
        intent.putExtra("player_status", isPlayerStart);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishFromOther);
        if (isBind == true) {
            unbindService(mServiceConnect);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(true);
                return true;
        }
        return false;
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Toast.makeText(this, "work " + permissions[0] , Toast.LENGTH_SHORT).show();*/
                } else {
                    /*Toast.makeText(this, "not work " + permissions[0] , Toast.LENGTH_SHORT).show();*/
                }
                break;
        }
    }

    private void alertDiolag(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_background));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.alert_dialog_activity_choose_lan);

        RadioButton radioBtnEn = (RadioButton) dialog.findViewById(R.id.radio_english);
        RadioButton radioBtnKm = (RadioButton) dialog.findViewById(R.id.radio_khmer);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);

        final MutiLanguage mutiLanguage = new MutiLanguage(this, this);
        String lang = mutiLanguage.getLanguageCurrent();

        if (lang.equals("km") || lang.isEmpty()) {
            radioBtnKm.setChecked(true);
        } else {
            radioBtnEn.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_english) {
                    mutiLanguage.setLanguage("en");
                    restartDefultForChangeLang();
                    dialog.dismiss();
                } else {
                    mutiLanguage.setLanguage("km");
                    restartDefultForChangeLang();
                    dialog.dismiss();
                }
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void restartDefultForChangeLang() {
        try {
            stopService(new Intent(MainActivity.this, ServiceMusic.class));
            mLocalBind.dismissNotification();
        } catch (NullPointerException e) {

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

  /*  class Starter implements Runnable {
        public void run() {
            animation.start();
        }
    }*/

    private void startAnimation() {
   /*     animation = new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.drawable.ic_audio), 100);
        animation.addFrame(getResources().getDrawable(R.drawable.ic_headphone_white), 100);
        animation.addFrame(getResources().getDrawable(R.drawable.ic_tv_white), 100);
        animation.setOneShot(false);
        imgAudio.setImageDrawable(animation);*/

        imgAudio.setBackgroundResource(R.drawable.ic_audio_anim);
        animation = (AnimationDrawable) imgAudio.getBackground();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new MutiLanguage(this).StartUpCheckLanguage();
    }
}
