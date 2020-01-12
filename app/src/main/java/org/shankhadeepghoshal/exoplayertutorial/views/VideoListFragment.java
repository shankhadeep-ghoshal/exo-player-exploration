package org.shankhadeepghoshal.exoplayertutorial.views;


import android.os.Bundle;

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
    public static final String TAG = "VideoListFragment";
    private static final String MP4_MEDIA_URL_1 = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4";
    private static final String MP4_MEDIA_URL_2 =
            "http://techslides.com/demos/sample-videos/small.mp4";
    private static final String MP4_MEDIA_URL_3 = "https://commondatastorage.googleapis" +
            ".com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4";
    private static final String MP4_MEDIA_URL_4 = "https://commondatastorage.googleapis" +
            ".com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";

    private static final String DASH_URL_1 = "https://media.axprod" +
            ".net/TestVectors/v9-MultiFormat/Clear/Manifest_1080p.mpd";
    private static final String DASH_URL_2 = "rdmedia.bbc.co.uk/dash/ondemand/bbb/2" +
            "/client_manifest-common_init.mpd";
    private static final String DASH_URL_3 = "https://dash.akamaized.net/dash264/TestCasesIOP33/adapatationSetSwitching/5/manifest.mpd";

    private List<Model> modelList;
    {
        modelList = new ArrayList<>(6);
        modelList.add(new Model(MP4_MEDIA_URL_1));
        modelList.add(new Model(DASH_URL_1));
        modelList.add(new Model(MP4_MEDIA_URL_2));
        modelList.add(new Model(MP4_MEDIA_URL_3));
        modelList.add(new Model(MP4_MEDIA_URL_4));
        modelList.add(new Model(DASH_URL_3));
    }

    private FullScreenCommsViewModel viewModel;
    private CustomRecyclerView recyclerView;

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

        setUpComponents();
        handleReturnFromFullScreenFragment();

        return view;
    }

    @Override
    public void onFullScreenButtonClick(String mediaUrl,
                                        long currentPlaybackTime,
                                        int currentWindowIndex) {
        TupleData<String, TupleData<Long, Integer>> td = new TupleData<>(mediaUrl,
                new TupleData<>(currentPlaybackTime, currentWindowIndex));
        viewModel.setPlaybackLiveData(td);
    }

    private void scrollToProvidedLocation(String mediaUrl,
                                          long currentPlaybackTime,
                                          int currentWindow) {
        List<Model> modelList = ((RecyclerViewAdapter) recyclerView.getAdapter()).getModelList();

        for (int i = 0; i < modelList.size(); i++) {
            Model model = modelList.get(i);
            if (model.getUrl().equals(mediaUrl)) {
                recyclerView.handleNavigationFromFullScreen(currentPlaybackTime, currentWindow, i);
                break;
            }
        }
    }

    private void setUpComponents() {
        recyclerView.setMediaUrlList(modelList);
        recyclerView.setAdapter(new RecyclerViewAdapter(modelList
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.requireContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setExoPlayerManager(((MainActivity)requireActivity()).getExoPlayerManager());
        recyclerView.setFullScreenButtonClickListener(this);
    }

    private void handleReturnFromFullScreenFragment() {
        Bundle returnData = getArguments();
        if (returnData != null) {
            boolean isBack = returnData.getBoolean("isBack", false);
            if (isBack) {
                String playbackUrl = returnData.getString("backMediaUrl");
                long playbackTime = returnData.getLong("backPlaybackTime");
                int currentWindow = returnData.getInt("backCurrentWindow");
                scrollToProvidedLocation(playbackUrl, playbackTime, currentWindow);
            }
        }
    }
}