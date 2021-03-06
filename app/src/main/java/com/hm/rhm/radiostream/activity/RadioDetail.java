package com.hm.rhm.radiostream.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.services.ServiceMusic;
import com.hm.rhm.radiostream.utils.BlurBuilder;
import com.hm.rhm.radiostream.utils.Constants;
import com.hm.rhm.radiostream.utils.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by soklundy on 5/8/2017.
 */

public class RadioDetail extends AppCompatActivity{

    private AudioManager audioManager;
    private boolean isBind;
    private boolean isConnect;
    private ServiceMusic.LocalBind mLocalBind;
    private LoadingDialog loadingDialog;
    private final BroadcastReceiver finishFromOther = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


    @BindView(R.id.seekBar1) SeekBar volumeSeekbar;
    @BindView(R.id.li_background) LinearLayout linearBackGround;
    @BindView(R.id.txt_title_fm) TextView txtTitleFm;
    @BindView(R.id.txt_num_fm) TextView txtNumFm;
    @BindView(R.id.img_channel) ImageView imgChannel;
    @BindView(R.id.img_play_pause) ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_detail);
        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);
        initBindingService();
        initBlurBackGround();
        bindTextWithData(getIntent().getStringExtra(Constants.CHHANELNAME));
        initVolumeControls();
        registerReceiver(finishFromOther, new IntentFilter("key_close"));
        checkBuuton();
        checkInternetConnection();
    }

    private void checkBuuton() {
        boolean isPlayerStart = getIntent().getBooleanExtra("player_status", false);
        if (isPlayerStart) {
            imageView.setImageResource(R.drawable.ic_pause);
        }else {
            imageView.setImageResource(R.drawable.ic_play);
        }
    }

    @OnClick(R.id.img_close)
    public void btnClose() {
        finish();
    }

    @OnClick(R.id.img_play_pause)
    public void btnPlay(View v) {
        if (isConnect == true) {
            if (mLocalBind.bindIsPlaying()) {
                mLocalBind.exoPlayerPause();
                imageView.setImageResource(R.drawable.ic_play);
            } else {
                mLocalBind.exoPlayerPlay();
                imageView.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    private void initBlurBackGround() {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.rhm);
        Bitmap blurredBitmap = BlurBuilder.blur(this, originalBitmap);
        linearBackGround.setBackground(new BitmapDrawable(getResources(), blurredBitmap ));
    }

    private void bindTextWithData(String channelName) {
        if (channelName.contains("95")) {
            imgChannel.setImageDrawable(getResources().getDrawable(R.drawable.ic_95));
            txtTitleFm.setText(R.string.rhm_fm);
            txtNumFm.setText("FM 95.7 MHz");
        }else {
            imgChannel.setImageDrawable(getResources().getDrawable(R.drawable.ic_104));
            txtTitleFm.setText(R.string.hm_fm);
            txtNumFm.setText("FM 104.5 MHz");
        }
    }

    private void initBindingService() {
        Intent intent = new Intent(this, ServiceMusic.class);
        bindService(intent, mServiceConnect, BIND_AUTO_CREATE);
    }

    private String splitChannelName(String channelName, int n) {
        String[] str =  channelName.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        return str[n];
    }

    private void initVolumeControls() {
        try {
            audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar = (SeekBar)findViewById(R.id.seekBar1);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {

                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
                        try {
                            loadingDialog.closeLoad();
                        }catch (NullPointerException e) {

                        }
                    }

                    if (playWhenReady == true) {
                        imageView.setImageResource(R.drawable.ic_pause);
                    }else {
                        imageView.setImageResource(R.drawable.ic_play);
                    }
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
                    try {
                        new AlertDialog.Builder(RadioDetail.this)
                                .setTitle(getResources().getString(R.string.can_load))
                                .setMessage(getResources().getString(R.string.check_inter))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                            loadingDialog.closeLoad();
                    }catch (Exception e){

                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishFromOther);
        if (isBind) {
            unbindService(mServiceConnect);
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
