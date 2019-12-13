package org.shankhadeepghoshal.exoplayertutorial;

import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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


    List<Model> modelList;
    {
        modelList = new ArrayList<>(3);
        modelList.add(new Model(MP4_MEDIA_URL_1));
        modelList.add(new Model(DASH_URL_1));
        modelList.add(new Model(MP4_MEDIA_URL_2));
    }

    private ExoPlayerManager exoPlayerManager;
    private CustomRecyclerView recyclerView;
    private int screenHeight;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setMediaUrlList(modelList);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenHeight = displayMetrics.heightPixels;
        this.screenWidth = displayMetrics.widthPixels;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23){
            exoPlayerManager = new ExoPlayerManager(this.getApplicationContext());

            recyclerView.setAdapter(new RecyclerViewAdapter(modelList, exoPlayerManager,
                    screenHeight, screenWidth));
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,
                    false));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || exoPlayerManager == null) {
            exoPlayerManager = new ExoPlayerManager(this.getApplicationContext());
            recyclerView.setAdapter(new RecyclerViewAdapter(modelList, exoPlayerManager,
                    screenHeight, screenWidth));
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,
                    false));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23 ) {
            exoPlayerManager.shutdownPlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            exoPlayerManager.shutdownPlayer();
        }
    }
}