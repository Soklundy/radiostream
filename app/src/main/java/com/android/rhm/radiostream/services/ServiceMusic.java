package com.android.rhm.radiostream.services;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.utils.LoadingDialog;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.IOException;

/**
 * Created by soklundy on 5/6/2017.
 */

public class ServiceMusic extends Service {

    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";
    private Handler mHandler = new Handler();
    private IBinder mBinder = new LocalBind();
    private ExoPlayer exoPlayer;
    private Context mContext;
    private DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
            userAgent, null,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
            true);

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = ServiceMusic.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Uri uri = Uri.parse(intent.getStringExtra("fm_url"));
            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                    mHandler, null);
            TrackSelector trackSelector = new DefaultTrackSelector(mHandler);
            DefaultLoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public class LocalBind extends Binder {

        public void stopRadio () {
            stopPlay();
        }

        public void playFm(String url) {
            Uri uri = Uri.parse(url);
            if (isPlaying()) {
                MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                        mHandler, null);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }
        }

        public void setLoadListener(ExoPlayer.EventListener eventListener) {
            exoPlayer.addListener(eventListener);
        }
    }

    private boolean isPlaying() {
        return exoPlayer != null && exoPlayer.getPlayWhenReady();
    }

    private void stopPlay() {
        if (isPlaying() != true) {
            exoPlayer.stop();
            exoPlayer.seekTo(0);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

}
