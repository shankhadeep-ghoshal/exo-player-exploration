package org.shankhadeepghoshal.exoplayertutorial.customviewcomponents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import org.shankhadeepghoshal.exoplayertutorial.R;
import org.shankhadeepghoshal.exoplayertutorial.model.Model;
import org.shankhadeepghoshal.exoplayertutorial.utils.ExoPlayerManager;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Model> modelList;

    private final int screenHeight;
    private final int screenWidth;

    public RecyclerViewAdapter(final List<Model> modelList,
                               final int screenHeight,
                               final int screenWidth) {
        this.modelList = modelList;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

/*
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        final Rect rect = new Rect();
        holder.getPlayerContainer().getGlobalVisibleRect(rect);
        final Rect screen = new Rect(0, 0, screenWidth/2, screenHeight/2);

        if (null == holder.getPlayerContainer() ||
                holder.getPlayerContainer().isShown() ||
                holder.getPlayerContainer().getVisibility() != View.VISIBLE ||
                !rect.intersect(screen)) {
            exoPlayerManager.getExoPlayer().setPlayWhenReady(false);

            long contentPosition = exoPlayerManager.getExoPlayer().getContentPosition();

            holder.setCurrentPlayPosition(contentPosition);
        }
    }
*/

/*
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        exoPlayerManager.getExoPlayer().seekTo(holder.getCurrentPlayPosition());
        exoPlayerManager.getExoPlayer().setPlayWhenReady(true);
    }
*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setParentTag();
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View parent;
        private FrameLayout playerContainer;
        private AppCompatImageButton fullScreenButton;
        private ProgressBar progressBar;
        private long currentPlayPosition = 0L;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerContainer = itemView.findViewById(R.id.media_container);
            progressBar = itemView.findViewById(R.id.progress_bar);
            fullScreenButton = itemView.findViewById(R.id.btn_full_screen);
            parent = itemView;
        }

        void setParentTag() {
            parent.setTag(this);
        }

        public FrameLayout getPlayerContainer() {
            return playerContainer;
        }

        public ProgressBar getProgressBar() {
            return this.progressBar;
        }

        public AppCompatImageButton getFullScreenButton() {
            return fullScreenButton;
        }

        public long getCurrentPlayPosition() {
            return currentPlayPosition;
        }

        public void setCurrentPlayPosition(long currentPlayPosition) {
            this.currentPlayPosition = currentPlayPosition;
        }
    }
}