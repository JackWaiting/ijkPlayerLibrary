package com.platomix.lib.playerengine.videoplayer.listener;

/**
 * Created by JackWaiting on 2017/6/22.
 */

public interface VideoListener {

    /**
     * 播放暂停
     */
    void onVideoStarted();

    /**
     * 继续播放
     */
    void onVideoPaused();

    /**
     * 播放完成
     */
    void onComplete();

    /**
     * 准备播放完成
     */
    void onPrepared();

    /**
     * 播放出错
     */
    void onError();

    void onInfo(int what, int extra);

}
