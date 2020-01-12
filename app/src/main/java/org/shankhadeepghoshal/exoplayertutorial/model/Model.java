package org.shankhadeepghoshal.exoplayertutorial.model;

import androidx.annotation.Nullable;

public class Model {
    private final String url;
    private long currentDuration;
    private int currentWindowIndex;

    public Model(String url) {
        this.url = url;
        this.currentDuration = 0L;
    }

    public void setCurrentDuration(long currentDuration) {
        this.currentDuration = currentDuration;
    }

    public long getCurrentDuration() {
        return currentDuration;
    }

    public int getCurrentWindowIndex() {
        return currentWindowIndex;
    }

    public void setCurrentWindowIndex(int currentWindowIndex) {
        this.currentWindowIndex = currentWindowIndex;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Model)) {
            return false;
        } else {
            Model newObj = (Model) obj;
            return newObj.getUrl().equals(this.getUrl());
        }
    }
}