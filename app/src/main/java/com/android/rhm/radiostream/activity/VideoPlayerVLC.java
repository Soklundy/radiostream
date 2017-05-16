package com.android.rhm.radiostream.activity;


import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.android.rhm.radiostream.R;
import com.android.rhm.radiostream.utils.LoadingDialog;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.media.VideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VideoPlayerVLC extends Activity implements IVLCVout.Callback{

    private Button button;
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";
    public final static String LOCATION = "com.sunvigor.soklundy.uploadradio";
    private String mFilePath;
    private LoadingDialog loadingDialog;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Receive path to play from intent
        /*mFilePath = "rtmp://111.92.240.134:1935/live/livestream";*/
        mFilePath = "rtmp://111.92.240.134:80/live/livestream";
        loadingDialog = new LoadingDialog(this);

        mSurface = (SurfaceView) findViewById(R.id.surface);
        holder = mSurface.getHolder();
        //holder.addCallback(this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPlayer(mFilePath);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if(holder == null || mSurface == null)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }


    private void replay() {
        createPlayer(mFilePath);
    }

    /*************
     * Player
     *************/
    private void createPlayer(String media) {
        releasePlayer();
        try {
            if (media.length() > 0) {
                loadingDialog.loading();
                /*toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();*/
            }

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            options.add("--http-reconnect");
            options.add("--network-caching="+6*1000);
            libvlc = new LibVLC(this, options);
            //libvlc.setOnHardwareAccelerationError(this);
            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, Uri.parse(media));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }

        mSurface.getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    Float p = event.getX()/size.x;
                    Long pos = (long) (mMediaPlayer.getLength() / p);
                    if (mMediaPlayer.isSeekable()) {
                        //mLibVLC.setTime( pos );
                        mMediaPlayer.setPosition(p);
                    } else {

                    }
                }

                return true;
            }
        });
    }

    // TODO: handle this cleaner
    private void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /*************
     * Events
     *************/

    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    @Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {

    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<VideoPlayerVLC> mOwner;

        public MyPlayerListener(VideoPlayerVLC owner) {
            mOwner = new WeakReference<VideoPlayerVLC>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            VideoPlayerVLC player = mOwner.get();
            try {
                switch(event.type) {
                    case MediaPlayer.Event.EndReached:
                        player.releasePlayer();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        //player.releasePlayer();
                        Toast toast = Toast.makeText(player, R.string.check_inter, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0,
                                0);
                        toast.show();
                        break;
                    case MediaPlayer.Event.Playing:
                        Toast.makeText(player, "playing", Toast.LENGTH_SHORT).show();
                        player.loadingDialog.closeLoad();
                        break;
                    case MediaPlayer.Event.Paused:
                        Toast.makeText(player, "Pause", Toast.LENGTH_SHORT).show();
                        player.loadingDialog.closeLoad();
                        break;
                    case MediaPlayer.Event.Stopped:
                        Toast.makeText(player, "Stopped", Toast.LENGTH_SHORT).show();
                        player.loadingDialog.closeLoad();
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException e) {

            }
        }
    }


    @Override
    public void onHardwareAccelerationError(IVLCVout vout) {
        // Handle errors with hardware acceleration
        this.releasePlayer();
        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    private void postOne() {
       /* Handler mHandler = new Handler();
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);
        Uri uri = Uri.parse("http://111.92.240.134:89/broadwavehigh.mp3");
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                mHandler, null);
        TrackSelector trackSelector = new DefaultTrackSelector(mHandler);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);*/
    }
}
