package com.android.rhm.radiostream.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.activity.MainActivity;
import com.android.rhm.radiostream.utils.Constants;
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
    private RemoteViews views;
    private Notification status;
    private static  final int NOTIFICATION_ID = 100;
    private NotificationManager notificationmanager;
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
        views = new RemoteViews(getPackageName(), R.layout.notification_layout);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                startPlay(intent.getStringExtra("fm_url"));
                showNotification(intent.getStringExtra(Constants.TITLESTATUS));
            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(Constants.ACTION.PAUSE_PLAY_ACTION)) {
                Toast.makeText(mContext, "stop/play", Toast.LENGTH_SHORT).show();
                if (isPlaying()) {
                    exoPlayer.setPlayWhenReady(false);
                    changeControlIconNotification(R.drawable.ic_play);
                }else {
                    exoPlayer.setPlayWhenReady(true);
                    changeControlIconNotification(R.drawable.ic_pause);
                }
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
                if (exoPlayer != null) {
                    exoPlayer.stop();
                    exoPlayer.seekTo(0);
                    exoPlayer.release();
                    exoPlayer = null;
                    mBinder = null;
                }
                stopService(new Intent(this, ServiceMusic.class));
                notificationmanager.cancel(NOTIFICATION_ID);
                sendBroadcast(new Intent("key_close"));
                closeStatusBar();
            }else if (intent.getAction().equals(Constants.ACTION.INTENTCONTENT)) {
                Toast.makeText(this, "intent", Toast.LENGTH_SHORT).show();
                Intent dialogIntent = new Intent(this, MainActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
                closeStatusBar();
            }
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    private void changeControlIconNotification(int resourceId) {
        views.setImageViewResource(R.id.ic_image, resourceId);
        status.contentView = views;
        notificationmanager.notify(NOTIFICATION_ID, status);
    }

    private void changeTextNIconControlNotification(String text, int resourceId) {
        views.setTextViewText(R.id.txt_status, text);
        views.setImageViewResource(R.id.ic_image, resourceId);
        status.contentView = views;
        notificationmanager.notify(NOTIFICATION_ID, status);
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

        }

        public void playFm(String url, String channelName) {
            Uri uri = Uri.parse(url);
            if (exoPlayer != null) {
                MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                        mHandler, null);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
                changeTextNIconControlNotification(channelName, R.drawable.ic_pause);
            }
        }

        public void setLoadListener(ExoPlayer.EventListener eventListener) {
            exoPlayer.addListener(eventListener);
        }
    }

    private boolean isPlaying() {
        return exoPlayer != null && exoPlayer.getPlayWhenReady();
    }

    private void startPlay(String url) {
        Uri uri = Uri.parse(url);
        if (!isPlaying()) {
            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                    mHandler, null);
            TrackSelector trackSelector = new DefaultTrackSelector(mHandler);
            DefaultLoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void stopPlay() {
        if (isPlaying() != true) {
            exoPlayer.stop();
            exoPlayer.seekTo(0);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void showNotification(String title) {
        // showing default album image
        /*views.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));*/

        Intent notificationIntent = new Intent(this, ServiceMusic.class);
        notificationIntent.setAction(Constants.ACTION.INTENTCONTENT);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        Intent pauseNplay = new Intent(this, ServiceMusic.class);
        pauseNplay.setAction(Constants.ACTION.PAUSE_PLAY_ACTION);
        PendingIntent pPauseNplayIntent = PendingIntent.getService(this, 0, pauseNplay, 0);

        Intent closeIntent = new Intent(this, ServiceMusic.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        views.setOnClickPendingIntent(R.id.ic_image, pPauseNplayIntent);
        views.setOnClickPendingIntent(R.id.ic_close, pCloseIntent);
        views.setOnClickPendingIntent(R.id.click_layout, pendingIntent);

        /*views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);*/

        views.setTextViewText(R.id.txt_status, title);
        views.setImageViewResource(R.id.ic_image, R.drawable.ic_pause);

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.cricle_hm;
        status.contentIntent = pendingIntent;
        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(NOTIFICATION_ID, status);
    }

    private void closeStatusBar() {
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.sendBroadcast(it);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notificationmanager.cancel(NOTIFICATION_ID);
    }
}
