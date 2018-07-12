package com.kevin.videoplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by Kevin Jern on 2018/7/13 5:23.
 */
public class VideoPlayerController extends FrameLayout implements
        View.OnClickListener {
    private static final String TAG = "VideoPlayerController";
    private Context mContext;
    private Button btnPre;
    private Button btnNext;
    private Button btnPlayOrPause;
    private VideoPlayerControl mVideoPlayerControl;


    public VideoPlayerController(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_controller, this);
        btnPre = findViewById(R.id.btn_left);
        btnNext = findViewById(R.id.btn_right);
        btnPlayOrPause = findViewById(R.id.btn_play_pause);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlayOrPause.setOnClickListener(this);
    }

    public void setVideoPlayer(VideoPlayerControl videoPlayerControl) {
        mVideoPlayerControl = videoPlayerControl;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                mVideoPlayerControl.play();
                btnPlayOrPause.setText("Pause");
                Log.d(TAG, "onClick: "+mVideoPlayerControl.isPlaying());
                if (mVideoPlayerControl.isPlaying()){
                    mVideoPlayerControl.pause();
                    btnPlayOrPause.setText("Play");
                }
                break;
        }
    }

    public void setControllerState(int state) {

    }
}
