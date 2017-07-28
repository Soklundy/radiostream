package com.hm.rhm.radiostream.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.hm.rhm.radiostream.R;
import com.hm.rhm.radiostream.utils.LoadingDialog;
import com.hm.rhm.radiostream.utils.MutiLanguage;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 7/22/2017.
 */

public class VideoExoplayer extends AppCompatActivity implements ExoPlayer.EventListener, AdaptiveMediaSourceEventListener {

    @BindView(R.id.simple_exoplayer)
    SimpleExoPlayerView mSimpleExoPlayerView;

    @BindView(R.id.activity_main_txt_resolu)
    TextView mResolution;

    @BindView(R.id.activity_main_txt_live)
    TextView mLive;

    private static final DefaultBandwidthMeter sBandWidthMeter = new DefaultBandwidthMeter();
    private static final int HD = 720;

    private SimpleExoPlayer mSimpleExoPlayer;
    private TelephonyManager mTelephonyManager;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_exoplayer);
        ButterKnife.bind(this);
        initTelephonyManger();
        loadingDialog = new LoadingDialog(this);
        new MutiLanguage(this).StartUpCheckLanguage();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                loadingDialog.loading();
                break;
            case ExoPlayer.STATE_ENDED:
                finish();
                break;
            case ExoPlayer.STATE_IDLE:
                loadingDialog.closeLoad();
                break;
            case ExoPlayer.STATE_READY:
                loadingDialog.closeLoad();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        releasePlayer();
        initPlayer();
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        if (trackFormat.height >= HD) {
            mResolution.setText("HD");
        } else {
            mResolution.setText(trackFormat.height + "");
        }
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
    }

    private void initPlayer() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        TrackSelection.Factory mTFactory = new AdaptiveTrackSelection.Factory(sBandWidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(mTFactory);
        LoadControl loadControl = new DefaultLoadControl();
        // 2. Create the player
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mSimpleExoPlayer.getCurrentWindowIndex();
        mSimpleExoPlayer.getCurrentPosition();
        mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);
        DataSource.Factory dataSourceFactory = buildDataSourceFactory(sBandWidthMeter);
        // This is the MediaSource representing the media to be played.
        Uri uri = Uri.parse(getIntent().getStringExtra("tv_url"));
        mLive.setText("Live Broacast • " + getIntent().getStringExtra("txt_channel") + " • Quality • ");
        MediaSource mMediaSource = new HlsMediaSource(uri, dataSourceFactory, mainHandler, this);
        mSimpleExoPlayer.prepare(mMediaSource);
        mSimpleExoPlayer.addListener(this);
        mSimpleExoPlayer.setPlayWhenReady(true);
        setUnRegisterPhoneStateListener();
    }

    /***
     * Build Data Source Factory using DefaultBandwidthMeter and HttpDataSource.Factory
     * @param bandwidthMeter
     * @return DataSource.Factory
     */
    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    /**
     * Build Http Data Source Factory using DefaultBandwidthMeter
     *
     * @param bandwidthMeter
     * @return HttpDataSource.Factory
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "Exoplayer"), bandwidthMeter);
    }

    private boolean isPlaying() {
        return mSimpleExoPlayer != null && mSimpleExoPlayer.getPlayWhenReady();
    }

    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
        setUnRegisterPhoneStateListener();
    }

    private void initTelephonyManger() {
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (mSimpleExoPlayer != null && isPlaying()) {
                    mSimpleExoPlayer.setPlayWhenReady(false);
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (mSimpleExoPlayer != null && !isPlaying()) {
                    mSimpleExoPlayer.setPlayWhenReady(true);
                }
                /*play*/
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                /*Toast.makeText(mContext, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();*/
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private void setPhoneStateListener() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void setUnRegisterPhoneStateListener() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
