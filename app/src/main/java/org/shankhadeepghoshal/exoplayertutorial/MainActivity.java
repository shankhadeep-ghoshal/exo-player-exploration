package org.shankhadeepghoshal.exoplayertutorial;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import org.shankhadeepghoshal.exoplayertutorial.utils.ExoPlayerManager;
import org.shankhadeepghoshal.exoplayertutorial.utils.TupleData;
import org.shankhadeepghoshal.exoplayertutorial.views.VideoFullScreenFragment;
import org.shankhadeepghoshal.exoplayertutorial.views.VideoListFragment;

public class MainActivity extends AppCompatActivity {
    private FullScreenCommsViewModel viewModel;
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
                .replace(R.id.main_activity_layout_space, listFragment, null )
                .commit();

        this.viewModel = ViewModelProviders.of(this).get(FullScreenCommsViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.viewModel.getPlaybackLiveData()
                .observe(this,
                        this::switchToFullScreenFragment);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.exoPlayerManager.shutdownPlayer();
    }

    public ExoPlayerManager getExoPlayerManager() {
        return this.exoPlayerManager;
    }

    private void switchToFullScreenFragment(final TupleData<String, Long> stringLongTupleData) {
        Bundle args = new Bundle();
        args.putString("mediaUrl", stringLongTupleData.getDataField1());
        args.putLong("playbackTime", stringLongTupleData.getDatafield2());

        VideoFullScreenFragment videoFrag = new VideoFullScreenFragment();
        videoFrag.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_layout_space, videoFrag, null)
                .addToBackStack(null)
                .commit();
    }
}