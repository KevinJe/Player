package com.kevin.videoplayer;

/**
 * Created by Kevin Jern on 2018/7/13 5:06.
 */
public interface VideoPlayerControl {
    void start();         //播放

    void pause();         //暂停

    void restart();       //再次播放

    void preVideo();      //上一个视频

    void nextVideo();     //下一个视频

    void release();       //释放资源

    boolean isPlaying();  //是否正在播放

    boolean isPaused();   //是否已经暂停

    boolean isCompleted();  //是否已经完成

    boolean isIdle();     //是否处于空闲

    int getPosition();   // 当前播放的进度

    int getDuration();   // 视频总的时长

    int getCurrentLoopCount();  // 得到当前循环次数
}
