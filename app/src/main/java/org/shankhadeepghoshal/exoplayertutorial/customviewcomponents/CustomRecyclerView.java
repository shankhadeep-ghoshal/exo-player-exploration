package org.shankhadeepghoshal.exoplayertutorial.customviewcomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import org.shankhadeepghoshal.exoplayertutorial.model.Model;
import org.shankhadeepghoshal.exoplayertutorial.utils.ExoPlayerManager;

import java.util.List;

public class CustomRecyclerView extends RecyclerView {
    private static final String TAG = "CUSTOM_RV";

    private boolean isFirstTime = true;
    private boolean isVideoViewAdded;
    private boolean isVideoPlaying;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;

    private List<Model> mediaUrlList;
    private FullScreenButtonListener fullScreenButtonListener;
    private ExoPlayerManager exoPlayerManager;

    private Context context;
    private View viewHolder;
    private PlayerView videoPlayerView;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    private AppCompatImageButton fullScreenButton;
    private int targetPosition;

    private final OnClickListener onFullScreenButtonClickListener = v ->
            fullScreenButtonListener
                    .onFullScreenButtonClick(mediaUrlList.get(targetPosition).getUrl(),
                    this.exoPlayerManager.pausePlayerAndGetCurrentRunningTime());

    public CustomRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= 0.45;                               // Lower is slower
        return super.fling(velocityX, velocityY);
    }

    public void setMediaUrlList(List<Model> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }

    public void setFullScreenButtonClickListener(FullScreenButtonListener fullScreenButtonListener) {
        this.fullScreenButtonListener = fullScreenButtonListener;
    }

    public void setExoPlayerManager(final ExoPlayerManager exoPlayerManager) {
        this.exoPlayerManager = exoPlayerManager;
        addExoPlayerListener();
    }

    private void init(Context context) {
        this.context = context;
        this.videoPlayerView = new PlayerView(context);

        addAnOnScrollListener();
        addAChildAttachedStateListener();
    }

    private void addExoPlayerListener() {
        exoPlayerManager
                .getExoPlayer()
                .addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        switch (playbackState) {
                            case Player.STATE_BUFFERING : {
                                if (progressBar != null) {
                                    progressBar.setVisibility(VISIBLE);
                                }
                                break;
                            }
                            case Player.STATE_ENDED : {
                                exoPlayerManager.getExoPlayer().seekTo(0);
                                break;
                            }
                            case Player.STATE_READY : {
                                if (progressBar != null) {
                                    progressBar.setVisibility(GONE);
                                }

                                if (!isVideoViewAdded) {
                                    addVideoView();
                                    Log.d(TAG, "onPlayerStateChanged: " + targetPosition);
                                }
                                break;
                            }
                        }
                    }
                });
    }

    private void addAChildAttachedStateListener() {
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Log.d(TAG, "onChildViewAttachedToWindow: " + targetPosition);
                if (isFirstTime && targetPosition == 0) {
                    if (frameLayout == null) {
                        setUpViews(0);
                    }
                    if (frameLayout.getChildCount() < 1) {
                        addVideoView();
                    }

                    playExoVideo(mediaUrlList.get(0));
                    isFirstTime = false;
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Log.d(TAG, "onChildViewDetachedFromWindow: " + targetPosition);
                if (viewHolder != null && viewHolder.equals(view)) {
                    if (isVideoPlaying) {
                        exoPlayerManager.pausePlayer();
                        mediaUrlList.get(targetPosition)
                                .setCurrentDuration(exoPlayerManager.getCurrentPlaybackTime());
                        Log.d(TAG, "onChildViewDetachedFromWindow: Player Paused at position: "
                                        + targetPosition);
                        mediaUrlList.get(targetPosition)
                                .setCurrentWindowIndex(exoPlayerManager.getCurrentWindowIndex());
                    } else if (exoPlayerManager.getExoPlayer().getPlaybackState()
                            == Player.STATE_BUFFERING) {
                        exoPlayerManager.getExoPlayer().stop(true);
                        Log.d(TAG,
                                "onChildViewDetachedFromWindow: Player was buffering at: "
                                        + targetPosition);
                    }
                    resetVideoView();
                }
                isVideoPlaying = false;
            }
        });
    }

    private void addAnOnScrollListener() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING)) {
                    if(!isVideoPlaying) {
                        if (!recyclerView.canScrollVertically(1)) playVideo(true);
                        else playVideo(false);
                    }
                }
            }
        });
    }

    private void addVideoView() {
        if (frameLayout != null) {
            this.exoPlayerManager.setUpPlayerView(this.videoPlayerView);
            this.frameLayout.addView(this.videoPlayerView);
            this.isVideoViewAdded = true;
            this.videoPlayerView.requestFocus();
            this.videoPlayerView.setVisibility(VISIBLE);
            this.videoPlayerView.setAlpha(1);
            Log.d(TAG, "addVideoView: " + targetPosition);
        }
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            this.videoPlayerView.setPlayer(null);
            this.exoPlayerManager.getExoPlayer().stop(true);
            Log.d(TAG, "resetVideoView: " + targetPosition);
            removeVideoView(this.videoPlayerView);
            this.videoPlayerView.setVisibility(GONE);
            this.isVideoPlaying = false;
        }
    }

    private void removeVideoView(PlayerView videoPlayerView) {
        ViewGroup parent = (ViewGroup) videoPlayerView.getParent();
        if (parent == null) {
            Log.d(TAG, "removeVideoView: parent is null at: " + targetPosition);
            return;
        }

        int index = parent.indexOfChild(videoPlayerView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            Log.d(TAG, "removeVideoView: " + targetPosition);
        }
    }

    private void playVideo(boolean isEndOfList) {
        if (!isEndOfList) {
            setTargetPositionIfNotLastPosition();
        } else {
            targetPosition = mediaUrlList.size() - 1;
        }

        int currentPosition = targetPosition - ((LinearLayoutManager) getLayoutManager())
                .findFirstVisibleItemPosition();

        setUpViews(currentPosition);
        playExoVideo(this.mediaUrlList.get(this.targetPosition));
    }

    private void setUpViews(int currentPosition) {
        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        setUpViewHolderElements(child);
    }

    private void setUpViewHolderElements(View view) {
        RecyclerViewAdapter.ViewHolder viewHolder = (RecyclerViewAdapter.ViewHolder) view.getTag();
        this.viewHolder = viewHolder.itemView;
        this.frameLayout = viewHolder.getPlayerContainer();
        this.progressBar = viewHolder.getProgressBar();
        this.fullScreenButton = viewHolder.getFullScreenButton();
        this.fullScreenButton.setOnClickListener(onFullScreenButtonClickListener);
    }

    private void playExoVideo(Model dataForPlaying) {
        this.exoPlayerManager.playVideo(dataForPlaying.getUrl(),
                dataForPlaying.getCurrentWindowIndex(),
                dataForPlaying.getCurrentDuration());
        this.isVideoPlaying = true;
        Log.d(TAG, "playingAtPosition: " + targetPosition);
    }

    private void setTargetPositionIfNotLastPosition() {
        int startPosition = ((LinearLayoutManager) getLayoutManager())
                .findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager())
                .findLastVisibleItemPosition();

        // if there is more than 2 list-items on the screen, set the difference to be 1
        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1;
        }

        // something is wrong. return.
        if (startPosition < 0 || endPosition < 0) {
            return;
        }

        // if there is more than 1 list-item on the screen
        if (startPosition != endPosition) {
            int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
            int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

            targetPosition = startPositionVideoHeight > endPositionVideoHeight
                    ? startPosition
                    : endPosition;
        } else {
            targetPosition = startPosition;
        }
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager())
                .findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    public interface FullScreenButtonListener {
        void onFullScreenButtonClick(String mediaUrl, long currentPlaybackTime);
    }
}