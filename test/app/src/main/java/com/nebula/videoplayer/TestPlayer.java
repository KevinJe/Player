package com.nebula.videoplayer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestPlayer extends FrameLayout implements VideoPlayerControl, TextureView.SurfaceTextureListener {

    public static final int STATE_ERROR = -1;          // 播放错误
    public static final int STATE_IDLE = 0;            // 播放未开始
    public static final int STATE_PREPARED = 2;        // 播放准备就绪
    public static final int STATE_PLAYING = 3;         // 正在播放
    public static final int STATE_PAUSED = 4;          // 暂停播放
    private static final int STATE_COMPLETED = 5;       // 播放完成
    /**
     * 视频播放容器
     */
    private FrameLayout mContainer;
    // context
    private Context mContext;
    // uri
    private String mUri;
    // mediaplayer
    private MediaPlayer mMediaPlayer;
    private int mCurrentState = STATE_IDLE; //播放状态

    private VideoPlayerController mController;

    private MediaPlayer mFirstPlayer, //负责播放进入视频播放界面后的第一段视频
            mNextPlayer, //负责一段视频播放结束后，播放下一段视频
            mCachePlayer, //负责setNextMediaPlayer的player缓存对象
            mCurrentPlayer; //负责当前播放视频段落的player对象

    // 视频列表
    private List<String> mVideoList = new ArrayList<>();
    // 所有的player缓存
    private HashMap<String, MediaPlayer> playerCache = new HashMap<>();
    // 当前的视频index
    private int currentIndex;
    // TextureView
    private TextureView mTextureView;
    // SurfaceTexture
    private SurfaceTexture mSurfaceTexture;

    public TestPlayer(@NonNull Context context) {
        this(context, null);
    }

    public TestPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, lp);
    }

    /**
     * 关联视频控制器
     */
    public void setController(VideoPlayerController controller) {
        mController = controller;
        mController.setVideoPlayer(this);
        updateVideoPlayerState();
        mContainer.removeView(mController);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mController, lp);
    }

    /**
     * 视频的连接
     *
     * @param uri
     */
    public void setPlayUri(String uri) {
        mUri = uri;
    }

    public void setPlayUri(List<String> uriList) {
        this.mVideoList = uriList;
    }

    /**
     * 初始化mediaPlayer
     */
    private void mediaPlayerInit() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
        }
    }

    /**
     * MediaPlayer准备好播放监听
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentState = STATE_PREPARED;
            updateVideoPlayerState();
        }
    };

    /**
     * mediaPlayer播放完成监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED; //播放完成
            updateVideoPlayerState();
        }
    };

    /**
     * Mediaplayer播放错误监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                //播放器开始渲染
                mCurrentState = STATE_PLAYING;
                updateVideoPlayerState();
            }
            return true;
        }
    };

    /**
     * 让mediaPlayer播放
     */
    private void mediaPlayerStart() {
        try {
            //设置数据源
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUri));
            //设置surface
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            //异步网络准备
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARED;
            updateVideoPlayerState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化TextureView
     */
    private void textureViewInit() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * 将TextureView添加进去
     */
    private void textureViewAdd() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mContainer.addView(mTextureView, 0, params); //mController在mTextureView上面
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE ||
                mCurrentState == STATE_ERROR
                || mCurrentState == STATE_COMPLETED ||
                mCurrentState == STATE_PAUSED) {
            mediaPlayerInit();
            textureViewInit();
            textureViewAdd();
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                updateVideoPlayerState();
            }
        }
    }

    /**
     * 重新播放
     */
    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            updateVideoPlayerState();
        }
    }

    /**
     * 上一个视频
     */
    @Override
    public void preVideo() {

    }

    /**
     * 下一个视频
     */
    @Override
    public void nextVideo() {

    }

    /**
     * 更新当前的状态
     */
    private void updateVideoPlayerState() {
        mController.setControllerState(mCurrentState);
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        if (mController != null) {
            mController.reset();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            mediaPlayerStart();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
