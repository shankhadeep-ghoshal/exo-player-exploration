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

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Model> modelList;

    public RecyclerViewAdapter(final List<Model> modelList) {
        this.modelList = modelList;
    }

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

    public List<Model> getModelList() {
        return modelList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View parent;
        private FrameLayout playerContainer;
        private AppCompatImageButton fullScreenButton;
        private ProgressBar progressBar;

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

        FrameLayout getPlayerContainer() {
            return playerContainer;
        }

        ProgressBar getProgressBar() {
            return this.progressBar;
        }

        AppCompatImageButton getFullScreenButton() {
            return fullScreenButton;
        }
    }
}