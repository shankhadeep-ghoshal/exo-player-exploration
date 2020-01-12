package org.shankhadeepghoshal.exoplayertutorial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.shankhadeepghoshal.exoplayertutorial.utils.TupleData;

public class FullScreenCommsViewModel extends ViewModel {
    private final MutableLiveData<TupleData<String, TupleData<Long, Integer>>> playbackLiveData
            = new MutableLiveData<>();

    private final MutableLiveData<TupleData<String, TupleData<Long, Integer>>>
            fromFullScreenToListFragmentDataTransfer = new MutableLiveData<>();

    public void setPlaybackLiveData(TupleData<String, TupleData<Long, Integer>> playbackLiveData) {
        this.playbackLiveData.setValue(playbackLiveData);
    }

    public void setFromFullScreenToListFragmentDataTransfer(TupleData<String,
            TupleData<Long, Integer>> data) {
        this.fromFullScreenToListFragmentDataTransfer.setValue(data);
    }

    public LiveData<TupleData<String, TupleData<Long, Integer>>> getPlaybackLiveData() {
        return this.playbackLiveData;
    }

    public MutableLiveData<TupleData<String, TupleData<Long, Integer>>>
    getFromFullScreenToListFragmentDataTransfer() {
        return fromFullScreenToListFragmentDataTransfer;
    }
}