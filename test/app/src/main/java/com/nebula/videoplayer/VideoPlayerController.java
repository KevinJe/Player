package com.nebula.videoplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 在这里控制视频的播放与暂停 上一个视频  下一个视频
 */
public class VideoPlayerController extends FrameLayout implements
        View.OnClickListener {
    private static final String TAG = "VideoPlayerController";
    private Context mContext;
    // 上一个视频
    private Button btnPre;
    // 下一个视频
    private Button btnNext;
    // 重新播放或者暂停
    private Button btnPlayOrPause;
    // 播放器控制器
    private VideoPlayerControl mVideoPlayerControl;
    // 暂停层
    private RelativeLayout rlPause;
    // 暂停层中的播放
    private Button btnPlay;
    // 暂停层中的退出
    private Button btnExit;


    public VideoPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_controller, this);
        btnPre = findViewById(R.id.btn_left);
        btnNext = findViewById(R.id.btn_right);
        btnPlayOrPause = findViewById(R.id.btn_play_pause);
        rlPause = findViewById(R.id.rl_pause);
        btnPlay = findViewById(R.id.btn_play);
        btnExit = findViewById(R.id.btn_exit);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPlayOrPause.setOnClickListener(this);
    }

    public void setVideoPlayer(VideoPlayerControl videoPlayerControl) {
        mVideoPlayerControl = videoPlayerControl;
        mVideoPlayerControl.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                Log.d(TAG, "onClick: 点击了暂停");
                playOrPause();
                break;
            case R.id.btn_left:
                mVideoPlayerControl.preVideo();
                break;
            case R.id.btn_right:
                mVideoPlayerControl.nextVideo();
                break;
            case R.id.btn_play:
                rlPause.setVisibility(GONE);
                playOrPause();
                break;
            case R.id.btn_exit:
                Toast.makeText(mContext, "你点击了退出", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void playOrPause() {
        if (mVideoPlayerControl.isPlaying() || mVideoPlayerControl.isCompleted()) {
            mVideoPlayerControl.pause();
        } else if (mVideoPlayerControl.isPaused() || mVideoPlayerControl.isCompleted()) {
            mVideoPlayerControl.restart();
        }
    }

    public void setControllerState(int state) {

    }

    public void reset() {

    }

    public void onPause() {
        if (mVideoPlayerControl.isIdle()) {
            return;
        }
        mVideoPlayerControl.pause();
    }

    public void onRestart() {
        if (mVideoPlayerControl.isIdle()) {
            return;
        }
        mVideoPlayerControl.restart();
    }
}
