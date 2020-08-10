package lab.nicc.kioskyoungcheon.ExoPlayerPackage;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by user on 2020-02-07.
 */

public class MyExoPlayer {
    PlayerView playerView;
    RelativeLayout playerViewLayout;
    RelativeLayout mainLayout;
    ImageView imageView;
    RelativeLayout imageViewLayout;
    SimpleExoPlayer player;
    boolean playWhenReady = true;
    int currnetWindow = 0;
    long playbackPosition = 0;
    DefaultTrackSelector trackSelector;
    Context context;
    final String TAG = "MY_EXO_PLAYER";
    boolean localVideoMode;

    Runnable imageRunnable;
    Handler imageHandler;

    public void bindExoPlayer(RelativeLayout mainLayout, PlayerView playerView, RelativeLayout playerViewLayout, ImageView imageView, RelativeLayout imageViewLayout, Context context) {
        this.mainLayout = mainLayout;
        this.imageView = imageView;
        this.imageViewLayout = imageViewLayout;
        this.playerView = playerView;
        this.playerViewLayout = playerViewLayout;
        trackSelector = new DefaultTrackSelector();
        this.context = context;
    }

    private static class LazyHolder {
        public static final MyExoPlayer INSTANCE = new MyExoPlayer();
    }

    public static MyExoPlayer getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void initializePlayer(final String[] sample, int index, final boolean localVideoMode, final String serverAddress) {
        mainLayout.setBackgroundColor(0xFF000000);
        this.localVideoMode = localVideoMode;
        Log.d(TAG, "initializePlayer: init");
        if (index >= sample.length) {
            index = 0;
        }
        if (sample[index].contains(".mp4")) {
            imageViewLayout.setVisibility(INVISIBLE);
            playerViewLayout.setVisibility(VISIBLE);

            Log.d(TAG, "initializePlayer: index: " + index + "sample.length: " + sample.length);

            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                playerView.setPlayer(player);
            }

            playerView.hideController();
            trustX509Certificate();

            MediaSource mediaSource;

            Log.d(TAG, "initializePlayer: index: " + index);

            mediaSource = getMediaSource(sample[index], localVideoMode, serverAddress);
            final int finalIndex = index + 1;
            Player.EventListener eventListener = new Player.EventListener() {

                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        releasePlayer();
                        initializePlayer(sample, finalIndex, localVideoMode, serverAddress);
                        Log.d(TAG, "onPlayerStateChanged: state final Index" + finalIndex);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    releasePlayer();
                    initializePlayer(sample, finalIndex, localVideoMode, serverAddress);
                    Log.d(TAG, "onPlayerStateChanged: state");
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
            };
            player.prepare(mediaSource, true, false);
            player.addListener(eventListener);
            player.setPlayWhenReady(true);
        } else if (sample[index].equals("no_layout")) {
            mainLayout.setBackgroundColor(0x00000000);
            imageViewLayout.setVisibility(INVISIBLE);
            playerViewLayout.setVisibility(INVISIBLE);
            final int finalIndex = index + 1;
            imageHandler = new Handler();
            imageRunnable = new Runnable() {
                @Override
                public void run() {
                    initializePlayer(sample, finalIndex, localVideoMode, serverAddress);
                }
            };
            imageHandler.postDelayed(imageRunnable, 10000);
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/video/", sample[index]);
            if (!file.exists())
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/gallery/", sample[index]);
            try {
                Glide.with(context).load(file)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .dontAnimate()
                        .centerCrop()
                        .into(imageView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                try {
                    releaseView();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
            imageViewLayout.setVisibility(VISIBLE);
            playerViewLayout.setVisibility(INVISIBLE);
            final int finalIndex = index + 1;
            imageHandler = new Handler();
            imageRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        initializePlayer(sample, finalIndex, localVideoMode, serverAddress);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        try {
                            releaseView();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }
                }
            };
            imageHandler.postDelayed(imageRunnable, 10000);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currnetWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            playerView.setPlayer(null);
            player.release();
            player = null;
        }
    }

    public void releaseView() {
        releasePlayer();
        if (imageHandler != null) {
            imageHandler.removeCallbacksAndMessages(null);
        }
        imageHandler = null;
        imageRunnable = null;
    }

    MediaSource getMediaSource(String fileString, boolean localVideoMode, String serverAddress) {
        String downloadAddress = serverAddress + "resources/upload/movie/";
        String userAgent = Util.getUserAgent(context, context.getPackageName());
        if (!localVideoMode) {
            Log.d(TAG, "getMediaSource: downloadAddress + fileString: " + downloadAddress + fileString);
            return new ExtractorMediaSource
                    .Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(Uri.parse(downloadAddress + fileString));
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", fileString);
            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(context, userAgent)).createMediaSource(Uri.fromFile(file));
        }
    }

    void trustX509Certificate() {
        TrustManager[] dummyTrustManager = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, dummyTrustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
