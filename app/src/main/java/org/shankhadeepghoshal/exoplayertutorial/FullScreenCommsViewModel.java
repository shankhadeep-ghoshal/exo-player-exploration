package org.shankhadeepghoshal.exoplayertutorial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.shankhadeepghoshal.exoplayertutorial.utils.TupleData;

public class FullScreenCommsViewModel extends ViewModel {
    private final MutableLiveData<TupleData<String, Long>> playbackLiveData
            = new MutableLiveData<>();

    public void setPlaybackLiveData(TupleData<String, Long> playbackLiveData) {
        this.playbackLiveData.setValue(playbackLiveData);
    }

    public LiveData<TupleData<String, Long>> getPlaybackLiveData() {
        return this.playbackLiveData;
    }
}