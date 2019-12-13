package org.shankhadeepghoshal.exoplayertutorial;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager {
    private final Context context;

    private ExoPlayer exoPlayer;
    private PlayerView videoSurfaceView;

    public ExoPlayerManager(final Context context) {
        this.context = context;
        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context);
        this.exoPlayer.getAudioComponent().setVolume(0f);
    }

    public void setUpPlayerView(PlayerView playerView) {
        this.videoSurfaceView = playerView;

        this.videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        this.videoSurfaceView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        this.videoSurfaceView.setPlayer(exoPlayer);
        this.videoSurfaceView.setUseController(false);
    }

    public void pausePlayer() {
        this.exoPlayer.setPlayWhenReady(false);
        this.exoPlayer.getPlaybackState();
    }

    public void seekToPosition(final int currentWindowIndex, final long toPosition) {
        this.exoPlayer.seekTo(currentWindowIndex, toPosition);
    }

    public int getCurrentWindowIndex() {
        return exoPlayer.getCurrentWindowIndex();
    }

    public void playVideo(final String mediaUrl,
                          final int currentWindowIndex,
                          final long toPosition) {
        @C.ContentType int type = Util.inferContentType(mediaUrl);

        switch (type) {
            case C.TYPE_DASH: {
                playDASHVideo(mediaUrl, currentWindowIndex, toPosition);
                break;
            }

            case C.TYPE_OTHER: {
                playNormalVideo(mediaUrl, currentWindowIndex, toPosition);
                break;
            }

            case C.TYPE_HLS:
            case C.TYPE_SS:
            default: {
                throw new IllegalStateException("Unsupported Type");
            }
        }
    }

    public void shutdownPlayer() {
        if (this.exoPlayer != null) {
            this.exoPlayer.release();
            this.exoPlayer = null;
        }
    }

    public long getCurrentPlaybackTime() {
        return exoPlayer.getCurrentPosition();
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    private void playNormalVideo(final String mediaUrl,
                                 final int currentWindowIndex,
                                 final long toPosition) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this.context,
                Util.getUserAgent(context, "ExoPlayerTutorial"));
        MediaSource mediaSource = new ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl));

        prepareAndPlayExoMedia(currentWindowIndex, toPosition, mediaSource);
    }

    private void playDASHVideo(final String mediaUrl,
                               final int currentWindowIndex,
                               final long toPosition) {
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(context,
                        "ExoPlayerTutorial"));
        MediaSource mediaSource = new DashMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl));

        prepareAndPlayExoMedia(currentWindowIndex, toPosition, mediaSource);
    }

    private void prepareAndPlayExoMedia(int currentWindowIndex,
                                        long toPosition,
                                        MediaSource mediaSource) {
        this.exoPlayer.setPlayWhenReady(true);
        seekToPosition(currentWindowIndex, toPosition);
        this.exoPlayer.prepare(mediaSource, false, true);
    }
}