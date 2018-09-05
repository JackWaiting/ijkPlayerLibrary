package com.platomix.lib.playerengine.core.local;

import android.graphics.Bitmap;


/**
 * 通知栏适配器
 *
 * @author jackwaiting
 */
public interface NotificationAdapter {
    /**
     * 音乐是否在播放
     * @return boolean
     */
    boolean isMusicPlaying();

    /**
     * 获取当前播放的音乐名称
     *
     * @return 当前播放的音乐名称
     */
    String getMusicName();

    /**
     * 获取当前的的歌曲描述
     *
     * @return 当前的的歌曲描述
     */
    String getArtistName();

    void setNotificationSongNext();

    void setNotificationSongPre();

    void setNotificationSongToggle();
    void setNotificationSongStop();



    /**
     * 加载音乐图片
     *
     * @param listener 加载音乐图片监听
     */
    void loadMusicImage(ImageLoadListener listener);

    interface ImageLoadListener {
        void onImageLoaded(String imageUri, Bitmap bitmap);
    }
}
