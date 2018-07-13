package com.nebula.videoplayer;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    // player
    private VideoPlayer videoPlayer;
    // player 控制器
    private VideoPlayerController playerController;
    // 视频列表
    private List<String> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initUri();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoPlayer = findViewById(R.id.video_player);
        playerController = new VideoPlayerController(this);
        videoPlayer.setPlayUri(videoList);
        videoPlayer.setController(playerController);
    }

    private void initUri() {
        videoList.add("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");
        videoList.add("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4");
        videoList.add("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerController.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerController.onRestart();
    }
}
