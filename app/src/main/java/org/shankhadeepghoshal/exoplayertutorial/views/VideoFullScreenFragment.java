package org.shankhadeepghoshal.exoplayertutorial.views;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import org.shankhadeepghoshal.exoplayertutorial.MainActivity;
import org.shankhadeepghoshal.exoplayertutorial.R;
import org.shankhadeepghoshal.exoplayertutorial.utils.ExoPlayerManager;

public class VideoFullScreenFragment extends Fragment {

    private ExoPlayerManager exoPlayerManager;
    private PlayerView playerView;

    private String mediaUrl;
    private long playbackTime;

    public VideoFullScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view = inflater.inflate(R.layout.fragment_video_full_screen, container, false);
        Bundle playbackData = getArguments();

        if (playbackData != null) {
            this.mediaUrl = playbackData.getString("mediaUrl");
            this.playbackTime = playbackData.getLong("playbackTime");
        } else if (savedInstanceState != null) {
            this.mediaUrl = savedInstanceState.getString("mediaUrl");
            this.playbackTime = savedInstanceState.getLong("playbackTime");
        } else {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "Nothing to Play",
                    Toast.LENGTH_SHORT).show();
        }

        setUpComponents(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23 ) {
            this.playbackTime = exoPlayerManager.pausePlayerAndGetCurrentRunningTime();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            this.playbackTime = exoPlayerManager.pausePlayerAndGetCurrentRunningTime();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("mediaUrl", this.mediaUrl);
        outState.putLong("playbackTime", this.playbackTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setUpComponents(View view) {
        this.playerView = view.findViewById(R.id.full_screen_player_view);
        exoPlayerManager = ((MainActivity)requireActivity()).getExoPlayerManager();
        exoPlayerManager.setUpPlayerView(playerView);
        exoPlayerManager.playVideo(mediaUrl, 0, playbackTime);
    }
}