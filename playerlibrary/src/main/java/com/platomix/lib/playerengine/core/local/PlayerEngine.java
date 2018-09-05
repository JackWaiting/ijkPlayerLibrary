
package com.platomix.lib.playerengine.core.local;

import com.platomix.lib.playerengine.api.PlaybackMode;
import com.platomix.lib.playerengine.api.Playlist;
import com.platomix.lib.playerengine.core.PlayerListener;


/**
 * 播放引擎接口
 *
 * @author jackwaiting
 */
public interface PlayerEngine {

    /**
     * 设置播放列表
     *
     * @param playlist 播放列表
     */
    void setPlaylist(Playlist playlist);

    /**
     * 当前播放列表
     *
     * @return <code>Playlist</code> 实例 或者 <code>null</code>
     */
    Playlist getPlaylist();

    /**
     * 开始播放列表中的音乐
     */
    void play();

    void setPercent(int percent);

    /**
     * 是否正在播放
     *
     * @return boolean value
     */
    boolean isLocalPlaying();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 暂停播放
     */
    void pauseLocal();

    /**
     * 继续播放
     */
    void resume();

    /**
     * 播放／暂停间切换
     * @return 播放／暂停
     */
    boolean toggle();

    /**
     * 下一首
     */
    void next();

    /**
     * 上一首
     */
    void prev();

    /**
     * 设置播放进度
     *
     * @param percent 范围0-1000
     */
    void seekTo(int percent);

    /**
     * 播放列表中index位置
     *
     * @param index 列表中index位置
     */
    void skipTo(int index);

    /**
     * 设置音量
     *
     * @param leftVolume  0－1f
     * @param rightVolume 0-1f
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * 事件监听
     *
     * @param playerEngineListener PlayerListener监听
     * @see PlayerListener
     */
    void setListener(PlayerListener playerEngineListener);

    PlayerListener getListener();

    /**
     * 设置播放模式
     *
     * @param aMode 枚举类{@link PlaybackMode}
     */
    void setPlaybackMode(PlaybackMode aMode);

    /**
     * 获取播放模式
     *
     * @return 播放模式
     */
    PlaybackMode getPlaybackMode();

    /**
     * 是否保持唤醒，不进入休眠。当后台运行时，系统可能会进入休眠，为了保持服务在后台运行，需要设置wake mode<br>
     * 需要谨慎的使用此功能，因为会消耗电量。
     */
    void setWakeMode();

    /**
     * 获取当前播放歌曲的播放百分比
     *
     * @return 当前播放歌曲的播放百分比
     */
    int getCurrentPlayPercent();

    /**
     * 当开始或暂停时，是否淡入或淡出声音
     *
     * @param fade 是否淡入或淡出声音
     */
    void setFadeVolumeWhenStartOrPause(boolean fade);

    boolean isFadeVolumeWhenStartOrPause();

    void setSpeed(float speed);
}
