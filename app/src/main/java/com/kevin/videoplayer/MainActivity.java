package com.kevin.videoplayer;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback
        , View.OnClickListener
        , TextureView.SurfaceTextureListener {
    private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer firstPlayer, //负责播放进入视频播放界面后的第一段视频
            nextPlayer, //负责一段视频播放结束后，播放下一段视频
            changePlayer,
            cachePlayer, //负责setNextMediaPlayer的player缓存对象
            currentPlayer; //负责当前播放视频段落的player对象
    // 视频列表
    private List<String> videoList = new ArrayList<>();
    // 所有的player缓存
    private HashMap<String, MediaPlayer> playerCache = new HashMap<>();
    // 当前的视频index
    private int currentIndex;
    private Button btnLeft;
    private Button btnRight;
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.surface);
//        mTextureView = findViewById(R.id.text_ture);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
//        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        getVideoUrls();
        initFirstPlayer();
    }

    private void getVideoUrls() {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            videoList.add(absolutePath + File.separator + "video" + File.separator + "1.mp4");
//            videoList.add(absolutePath + File.separator + "video" + File.separator + "2.mp4");
//            videoList.add(absolutePath + File.separator + "video" + File.separator + "3.mp4");
//            videoList.add(absolutePath + File.separator + "video" + File.separator + "4.mp4");
//        }
        videoList.add("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");
        videoList.add("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4");
        videoList.add("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");
    }

    /**
     * 初始化第一个Player
     */
    private void initFirstPlayer() {
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 视频显示在surfaceView上
        firstPlayer.setDisplay(surfaceHolder);
//        firstPlayer.setSurface(new Surface(mSurfaceTexture));
        firstPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVideoComplete(mp);
            }
        });
        cachePlayer = firstPlayer;
        playerCache.put(String.valueOf(0), firstPlayer);
        currentPlayer = playerCache.get(String.valueOf(currentIndex));
        initNextPlayer();
        startFirstPlayer();
    }

    private void textureViewInit() {
    }

    /**
     * 当前视频播放完成
     */
    private void onVideoComplete(MediaPlayer player) {
        player.setDisplay(null);
//        player.setSurface(null);
        currentPlayer = playerCache.get(String.valueOf(++currentIndex));
        if (currentPlayer != null) {
            currentPlayer.setDisplay(surfaceHolder);
//            currentPlayer.setSurface(new Surface(mSurfaceTexture));
        } else {
            Toast.makeText(this, "视频播放完成", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 开始播放第一个
     */
    private void startFirstPlayer() {
        try {
            firstPlayer.setDataSource(videoList.get(currentIndex));
            firstPlayer.prepare();
            firstPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化下一个player
     */
    private void initNextPlayer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < videoList.size(); i++) {
                    nextPlayer = new MediaPlayer();
                    nextPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            onVideoComplete(mp);
                        }
                    });
                    try {
                        nextPlayer.setDataSource(videoList.get(i));
                        nextPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cachePlayer.setNextMediaPlayer(nextPlayer);
                    cachePlayer = nextPlayer;
                    playerCache.put(String.valueOf(i), nextPlayer);
                }
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
            }
            firstPlayer.release();
        }
        if (nextPlayer != null) {
            if (nextPlayer.isPlaying()) {
                nextPlayer.stop();
            }
            nextPlayer.release();
        }
        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                if (currentIndex > 0) {
                    if (currentPlayer != null) {
                        currentIndex--;
                        onVideoChanged(currentPlayer);
                    }
                } else {
                    Log.e(TAG, "onClick: 未能切换上一个");
                }
                break;
            case R.id.btn_right:
                Log.e(TAG, "onClick: " + (currentPlayer == null));
                if (currentIndex < videoList.size() - 1) {
                    if (currentPlayer != null) {
                        currentIndex++;
                        onVideoChanged(currentPlayer);
                    }
                } else {
                    Log.e(TAG, "onClick: 未能切换下一个");
                }
                break;

        }
    }

    private void onVideoChanged(MediaPlayer player) {
        changePlayer = playerCache.get(String.valueOf(currentIndex));
        if (changePlayer != null) {
            player.setDisplay(null);
//            player.setSurface(null);
            player.pause();
            changePlayer.setDisplay(surfaceHolder);
//            changePlayer.setSurface(new Surface(mSurfaceTexture));
            changePlayer.start();
            changePlayer.pause();
            changePlayer.seekTo(0);
            currentPlayer = changePlayer;
//            currentPlayer.reset();
//            try {
//                currentPlayer.setDataSource(videoList.get(currentIndex));
//                currentPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            currentPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    onVideoComplete(mp);
//                }
//            });
            currentPlayer.start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            getVideoUrls();
            initFirstPlayer();
            Log.d(TAG, "onSurfaceTextureAvailable: ");
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

