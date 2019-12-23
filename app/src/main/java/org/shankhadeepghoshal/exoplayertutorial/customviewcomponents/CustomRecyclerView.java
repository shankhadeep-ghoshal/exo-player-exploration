package org.shankhadeepghoshal.exoplayertutorial.customviewcomponents;

import android.content.Context;
import android.util.AttributeSet;
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

    public ExoPlayerManager getExoPlayerManager() {
        return this.exoPlayerManager;
    }

    public int getCurrentExecutingPosition() {
        return this.targetPosition;
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

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolder != null && viewHolder.equals(view)) {
                    exoPlayerManager.pausePlayer();
                    mediaUrlList.get(targetPosition)
                            .setCurrentDuration(exoPlayerManager.getCurrentPlaybackTime());
                    resetVideoView();
                    mediaUrlList.get(targetPosition)
                            .setCurrentWindowIndex(exoPlayerManager.getCurrentWindowIndex());
                }
            }
        });
    }

    private void addAnOnScrollListener() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING) && !isVideoPlaying) {
                    if (!recyclerView.canScrollVertically(1)) playVideo(true);
                    else playVideo(false);
                }
            }
        });
    }

    private void addVideoView() {
        if (frameLayout != null) {
            this.frameLayout.addView(videoPlayerView);
            this.isVideoViewAdded = true;
            this.videoPlayerView.requestFocus();
            this.videoPlayerView.setVisibility(VISIBLE);
            this.videoPlayerView.setAlpha(1);
        }
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(this.videoPlayerView);
            this.videoPlayerView.setVisibility(GONE);
            this.isVideoPlaying = false;
        }
    }

    private void removeVideoView(PlayerView videoPlayerView) {
        ViewGroup parent = (ViewGroup) videoPlayerView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoPlayerView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
        }
    }

    private void playVideo(boolean isEndOfList) {
        if (!isEndOfList) {
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
        } else {
            targetPosition = mediaUrlList.size() - 1;
        }

        int currentPosition = targetPosition - ((LinearLayoutManager) getLayoutManager())
                .findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        RecyclerViewAdapter.ViewHolder viewHolder = (RecyclerViewAdapter.ViewHolder) child.getTag();
        this.viewHolder = viewHolder.itemView;
        this.frameLayout = viewHolder.getPlayerContainer();
        this.progressBar = viewHolder.getProgressBar();
        this.fullScreenButton = viewHolder.getFullScreenButton();
        this.exoPlayerManager.setUpPlayerView(this.videoPlayerView);
        this.exoPlayerManager.playVideo(this.mediaUrlList.get(targetPosition).getUrl(),
                this.mediaUrlList.get(targetPosition).getCurrentWindowIndex(),
                this.mediaUrlList.get(targetPosition).getCurrentDuration());
        this.fullScreenButton.setOnClickListener(onFullScreenButtonClickListener);

        this.isVideoPlaying = true;
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