package org.shankhadeepghoshal.exoplayertutorial;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import org.shankhadeepghoshal.exoplayertutorial.utils.ExoPlayerManager;
import org.shankhadeepghoshal.exoplayertutorial.utils.TupleData;
import org.shankhadeepghoshal.exoplayertutorial.views.VideoFullScreenFragment;
import org.shankhadeepghoshal.exoplayertutorial.views.VideoListFragment;

public class MainActivity extends AppCompatActivity {
    private FullScreenCommsViewModel viewModelListToFullScreen;
    private FullScreenCommsViewModel viewModelFullScreenToList;
    private ExoPlayerManager exoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.exoPlayerManager = new ExoPlayerManager(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);

        VideoListFragment listFragment = VideoListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_layout_space, listFragment, VideoListFragment.TAG)
                .commit();

        this.viewModelListToFullScreen = ViewModelProviders
                .of(this)
                .get(FullScreenCommsViewModel.class);
        this.viewModelFullScreenToList = ViewModelProviders
                .of(this)
                .get(FullScreenCommsViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.viewModelListToFullScreen
                .getPlaybackLiveData()
                .observe(this, this::switchToFullScreenFragment);
        this.viewModelFullScreenToList
                .getFromFullScreenToListFragmentDataTransfer()
                .observe(this, this::switchToListFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.exoPlayerManager.shutdownPlayer();
    }

    public ExoPlayerManager getExoPlayerManager() {
        return this.exoPlayerManager;
    }

    private void switchToFullScreenFragment(final TupleData<String, TupleData<Long, Integer>>
                                                    stringLongTupleData) {
        Bundle args = new Bundle();
        args.putString("mediaUrl", stringLongTupleData.getDataField1());
        args.putLong("playbackTime", stringLongTupleData.getDataField2().getDataField1());
        args.putInt("windowIndex", stringLongTupleData.getDataField2().getDataField2());

        VideoFullScreenFragment videoFrag = new VideoFullScreenFragment();
        videoFrag.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_layout_space, videoFrag, VideoFullScreenFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private void switchToListFragment(final TupleData<String, TupleData<Long, Integer>>
                                              stringLongTupleData) {
        String mediaUrl = stringLongTupleData.getDataField1();
        long currentPlaybackTime = stringLongTupleData.getDataField2().getDataField1();
        int currentWindow = stringLongTupleData.getDataField2().getDataField2();

        VideoListFragment fragment = (VideoListFragment) getSupportFragmentManager()
                .findFragmentByTag(VideoListFragment.TAG);
        Bundle playbackDataArgs = new Bundle();
        playbackDataArgs.putBoolean("isBack", true);
        playbackDataArgs.putString("backMediaUrl", mediaUrl);
        playbackDataArgs.putInt("backCurrentWindow", currentWindow);
        playbackDataArgs.putLong("backPlaybackTime", currentPlaybackTime);
        fragment.setArguments(playbackDataArgs);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_layout_space, fragment, VideoListFragment.TAG)
                .commit();
    }
}