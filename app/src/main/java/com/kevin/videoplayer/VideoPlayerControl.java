package com.kevin.videoplayer;

/**
 * Created by Kevin Jern on 2018/7/13 5:06.
 */
public interface VideoPlayerControl {
    void play();         //播放

    void pause();         //暂停

    void preVideo();      //上一个视频

    void nextVideo();     //下一个视频

    void release();

    boolean isPlaying();
}
