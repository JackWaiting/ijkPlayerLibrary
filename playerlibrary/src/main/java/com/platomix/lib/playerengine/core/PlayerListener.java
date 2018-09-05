package com.platomix.lib.playerengine.core;


/**
 * 播放引擎事件监听接口
 */
public interface PlayerListener {

    /**
     * 音频开始播放回调
     *
     * @param uri 当前播放的音频的地址
     * @return 是否开始播放
     */
    boolean onTrackStart(String uri);

    /**
     * 播放的音频改变时的回调，在onTrackStart()之前被调用
     *
     * @param uri 改变后的音频地址
     */
    void onTrackChanged(String uri);

    /**
     * 播放进度回调
     *
     * @param uri             当前播放的音频的地址
     * @param percent         取值范围0-1000
     * @param currentDuration 已播放的时间，毫秒
     * @param duration        歌曲总时长，毫秒
     */
    void onTrackProgress(String uri, long percent, long currentDuration, long duration);

    /**
     * 缓冲回调
     *
     * @param percent 取值范围0-100
     * @param uri uri
     */
    void onTrackBuffering(String uri, int percent);

    /**
     * 播放器停止的回调
     *
     * @param uri 停止时的音频地址
     */
    void onTrackStop(String uri);

    /**
     * 暂停
     *
     * @param uri 被暂停的音频地址
     */
    void onTrackPause(String uri);

    /**
     * 发生错误
     *
     * @param uri 发生错误的音频地址
     * @param what 发生错误的 what 值
     * @param extra 发生错误的extra 值
     */
    void onTrackStreamError(String uri, int what, int extra);


    void onTrackHold(String uri);

}
