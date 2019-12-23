package org.shankhadeepghoshal.exoplayertutorial.views;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shankhadeepghoshal.exoplayertutorial.FullScreenCommsViewModel;
import org.shankhadeepghoshal.exoplayertutorial.MainActivity;
import org.shankhadeepghoshal.exoplayertutorial.customviewcomponents.CustomRecyclerView;
import org.shankhadeepghoshal.exoplayertutorial.R;
import org.shankhadeepghoshal.exoplayertutorial.customviewcomponents.RecyclerViewAdapter;
import org.shankhadeepghoshal.exoplayertutorial.model.Model;
import org.shankhadeepghoshal.exoplayertutorial.utils.TupleData;

import java.util.ArrayList;
import java.util.List;

public class VideoListFragment
        extends Fragment
        implements CustomRecyclerView.FullScreenButtonListener{

    private static final String MP4_MEDIA_URL_1 = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4";
    private static final String MP4_MEDIA_URL_2 = "http://techslides.com/demos/sample-videos/small.mp4";

    //"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes
    // .mp4";

    private static final String DASH_URL_1 = "https://media.axprod" +
            ".net/TestVectors/v9-MultiFormat/Clear/Manifest_1080p.mpd";
    private static final String DASH_URL_2 = "rdmedia.bbc.co.uk/dash/ondemand/bbb/2" +
            "/client_manifest-common_init.mpd";

    private static final String[] URL_LIST = new String[]
            {MP4_MEDIA_URL_1, DASH_URL_1, MP4_MEDIA_URL_2};

    private List<Model> modelList;
    {
        modelList = new ArrayList<>(3);
        modelList.add(new Model(MP4_MEDIA_URL_1));
        modelList.add(new Model(DASH_URL_1));
        modelList.add(new Model(MP4_MEDIA_URL_2));
    }

    private FullScreenCommsViewModel viewModel;
    private CustomRecyclerView recyclerView;

    private int screenHeight;
    private int screenWidth;

    private String currentPlayingUrl;
    private long currentPlaybackTime;
    private int currentPosition;

    public VideoListFragment() {
        // Required empty public constructor
    }

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(FullScreenCommsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenHeight = displayMetrics.heightPixels;
        this.screenWidth = displayMetrics.widthPixels;

        setUpComponents();

/*
        if (savedInstanceState != null) {
            handleSavedStuff(savedInstanceState);
        }
*/

        return view;
    }

/*
    private void handleSavedStuff(Bundle savedInstanceState) {
        int currentPosition = savedInstanceState.getInt("currentPosition");
        int currentPlaybackTime = savedInstanceState.getInt("playPosition");

        this.recyclerView.scrollToPosition(currentPosition);
        ((RecyclerViewAdapter.ViewHolder)this.recyclerView
                .findViewHolderForLayoutPosition(currentPosition))
                .setCurrentPlayPosition(currentPlaybackTime);
    }
*/

    @Override
    public void onFullScreenButtonClick(String mediaUrl, long currentPlaybackTime) {
        TupleData<String, Long> td = new TupleData<>(mediaUrl, currentPlaybackTime);
        viewModel.setPlaybackLiveData(td);
    }

/*
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long currentExecutionTimeOfPlayingVideo = this.recyclerView
                .getExoPlayerManager()
                .pausePlayerAndGetCurrentRunningTime();

        outState.putInt("currentPosition", this.currentPosition);
        outState.putLong("playPosition", this.currentPlaybackTime);
    }
*/

    private void setUpComponents() {
        recyclerView.setMediaUrlList(modelList);
        recyclerView.setAdapter(new RecyclerViewAdapter(modelList,
                screenHeight,
                screenWidth));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.requireContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setExoPlayerManager(((MainActivity)requireActivity()).getExoPlayerManager());
        recyclerView.setFullScreenButtonClickListener(this);
    }
}