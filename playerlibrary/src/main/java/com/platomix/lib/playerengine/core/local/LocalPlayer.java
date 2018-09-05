package com.platomix.lib.playerengine.core.local;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.view.WindowManager;

import com.platomix.lib.playerengine.api.PlaybackMode;
import com.platomix.lib.playerengine.api.Playlist;
import com.platomix.lib.playerengine.command.CommandFactory;
import com.platomix.lib.playerengine.command.CommandFactory.Command;
import com.platomix.lib.playerengine.command.CommandFactory.Extra;
import com.platomix.lib.playerengine.core.PlayerEngine;
import com.platomix.lib.playerengine.core.PlayerListener;
import com.platomix.lib.playerengine.util.MusicLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地与网络音乐播放控制类
 *
 * @author jackwaiting
 */
public final class LocalPlayer implements PlayerEngine {
    /**
     * 当前版本号
     */
    public static final String VERSION_CODE = "1.2.4";
    private static final String TAG = "LocalPlayer";
    private PlayerListener listenenr;
    private static LocalPlayer player;
    private static Context con;
    private PlaybackMode playbackMode;
    private Playlist playlist;
    private WifiLock wifiLock;
    private PlayerEngineImpl playerEngine;
    private boolean fadeVolume;
    static Class<? extends PlayerService> mServiceClass = PlayerService.class;
    private CommandFactory commandFactory;

    private LocalPlayer(Context context,
                        Class<? extends PlayerService> serviceClass) {
        if (serviceClass != null) {
            mServiceClass = serviceClass;
        }
        commandFactory = CommandFactory.getInstance(context);
        commandFactory.setTarget(mServiceClass);
        LocalPlayer.con = context;

        sendBaseCommand("first");
    }

    public void setNotificationAdapter(NotificationAdapter notificationAdapter) {
        PlayerService.setNotificationAdapter(notificationAdapter);
    }

    /**
     * 获取实例，如果为空，则初始化，同时会开启后台服务
     *
     * @param context context
     * @return LocalPlayer
     */
    public static LocalPlayer getInstance(Context context) {
        if (player == null || con == null) {
            Log.e(TAG, "注册LocalPlayer");
            player = new LocalPlayer(context, mServiceClass);
        }
        return player;
    }

    /**
     * 获取实例，如果为空，则初始化，同时会开启后台服务
     *
     * @param context      context
     * @param serviceClass serviceClass
     * @return LocalPlayer
     */
    public static LocalPlayer getInstance(Context context,
                                          Class<? extends PlayerService> serviceClass) {
        if (player == null || con == null) {
            player = new LocalPlayer(context, serviceClass);
        }
        return player;
    }

    /**
     * 获取实例，如果为null并不初始化
     *
     * @return LocalPlayer
     */
    public static LocalPlayer getInstance() {
        return player;
    }

    static Context getContext() {
        return con;
    }

    @Override
    public void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
        sendBaseCommand(Command.ACTION_PLAY_LIST);
    }

    @Override
    public Playlist getPlaylist() {
        return this.playlist;
    }

    void setPlayerEngine(PlayerEngineImpl playerEngine) {
        this.playerEngine = playerEngine;
    }

    public int getAudioSessionId() {
        if (playerEngine != null) {
            return playerEngine.getAudioSessionId();
        }
        return -1;
    }

    @Override
    public void play() {
        sendBaseCommand(Command.ACTION_PLAY);
    }

    @Override
    public void setPercent(int percent) {
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_SEEK_PERCENT, percent);
        commandFactory.sendCommand(Command.ACTION_SEEK_TO, map);
    }


    @Override
    public void pauseLocal() {
        sendBaseCommand(Command.ACTION_PAUSE);
    }

    @Override
    public void resume() {
        sendBaseCommand(Command.ACTION_RESUME);
    }

    @Override
    public boolean toggle() {
        sendBaseCommand(Command.ACTION_TOGGLE_PLAY);
        return PlayerEngineImpl.isPlay();
    }

    @Override
    public void seekTo(int percent) {
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_SEEK_PERCENT, percent);
        commandFactory.sendCommand(Command.ACTION_SEEK_TO, map);
    }

    @Override
    public boolean isLocalPlaying() {
        return PlayerEngineImpl.isPlay();
    }

    @Override
    public void next() {
        sendBaseCommand(Command.ACTION_NEXT);
    }

    @Override
    public void prev() {
        sendBaseCommand(Command.ACTION_PRE);
    }

    @Override
    public void skipTo(int index) {
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_SKIP_INDEX, index);
        commandFactory.sendCommand(Command.ACTION_SKIP_TO, map);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_LEFT_VOLUME, leftVolume);
        map.put(Extra.EXTRA_RIGHT_VOLUME, rightVolume);
        commandFactory.sendCommand(Command.ACTION_SET_VOLUME, map);
    }

    @Override
    public void setListener(PlayerListener playerListener) {
        this.listenenr = playerListener;
        sendBaseCommand(Command.ACTION_BIND_LISTENER);
    }

    @Override
    public PlayerListener getListener() {
        return listenenr;
    }

    @Override
    public void setPlaybackMode(PlaybackMode playmode) {
        playbackMode = playmode;
        sendBaseCommand(Command.ACTION_SET_PLAYMODE);
    }

    @Override
    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    @Override
    public void setWakeMode() {
        sendBaseCommand(Command.ACTION_WAKE_MODE);
    }

    /**
     * 屏幕常亮
     *
     * @param act 传入Activity
     */
    public void setScreenOn(Activity act) {
        act.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 获取当前播放的音频地址
     *
     * @return String 音频地址，如果列表为空则返回null
     */
    public String getCurrentTrackUri() {
        if (playlist != null) {
            return playlist.getSelectedUri();
        }
        return null;
    }

    private int startIndex;

    /**
     * 设置播放列表起始位置
     *
     * @param index 索引
     */
    public void setStartIndex(int index) {
        startIndex = index;
        if (playlist != null) {
            playlist.select(index);
        }
    }

    /**
     * 获取当前歌曲在列表中的位置
     *
     * @return 当前歌曲在列表中的位置
     */
    public int getCurrentIndex() {
        if (playlist != null) {
            return playlist.getSelectedIndex();
        }
        return -1;
    }

    /**
     * 加载本地音乐
     *
     * @param minDuration 过滤掉小于此值的音频，单位毫秒
     * @param callback    加载完成回调
     * @param pathFilter    过滤
     */
    public void getLocalPlaylist(int minDuration,
                                 final LoadMusicCallback callback, String pathFilter) {
        if (con == null || con.getContentResolver() == null) {
            return;
        }
        MusicLoader loader = MusicLoader.instance(con.getContentResolver());
        loader.setMinMusicDuration(minDuration);
        loader.loadMusic(new LoadMusicCallback() {
            @Override
            public void onLoadMusic(List<PlaylistEntity> list) {
                callback.onLoadMusic(list);
            }
        }, pathFilter);
    }

    /**
     * 在设备进入休眠后，wifi会被关闭，如果播放的是网络歌曲，不希望wifi在休眠时关闭，需要设置wifi lock。<br>
     * 在不需要使用wifi时，要释放wifi lock，调用{@link #releaseWifiLock()}方法
     */
    public void acquireWifiLock() {
        WifiLock wifiLock = ((WifiManager) con
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();
    }

    /**
     * 在不需要使用wifi时，释放wifi lock
     */
    public void releaseWifiLock() {
        if (wifiLock != null) {
            wifiLock.release();
        }
    }

    /**
     * 停止播放，释放资源。关闭通知栏
     */
    public void stop() {
        stop(true);
    }

    /**
     * 停止播放，释放资源
     *
     * @param closeNotification 是否关闭通知栏
     */
    private void stop(boolean closeNotification) {
        if (con == null) {
            return;
        }
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_CLOSE_NOTIFICATION, true); //此处设置为false，存在问题。先默认true
        commandFactory.sendCommand(Command.ACTION_STOP, map);
        player = null;
        con = null;
    }

    @Override
    public int getCurrentPlayPercent() {
        return PlayerService.getCurrentPlayPosition();
    }

    @Override
    public void setFadeVolumeWhenStartOrPause(boolean fade) {
        this.fadeVolume = fade;
        sendBaseCommand(Command.ACTION_FADE_VOLUME);
    }

    @Override
    public boolean isFadeVolumeWhenStartOrPause() {
        return fadeVolume;
    }

    @Override
    public void setSpeed(float speed) {

        Log.i(TAG, "我改变了倍数" + speed + "LocalPlayer");
        Map<String, Object> map = getExtraMap();
        map.put(Extra.EXTRA_SPEED, speed);
        commandFactory.sendCommand(Command.ACTION_SPEED, map);
    }

    private boolean playnextWhenError = false;

    /**
     * 发生错误时是否自动切换到下一曲
     * @param playnextWhenError 是否自动切换到下一曲
     */
    public void setPlayNextWhenError(boolean playnextWhenError) {
        this.playnextWhenError = playnextWhenError;
        sendBaseCommand(Command.ACTION_PLAY_NEXT_WHEN_ERROR);
    }

    public boolean isPlaynextWhenError() {
        return playnextWhenError;
    }

    private void sendBaseCommand(String command) {
        Map<String, Object> map = getExtraMap();
        commandFactory.sendCommand(command, map);
    }

    private Map<String, Object> getExtraMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Extra.EXTRA_FROM, TAG);
        return map;
    }

    static Class<? extends BroadcastReceiver> mediaButtonIntenerReceiverCls = MediaButtonIntentReceiver.class;

    /**
     * @param cls MediaButtonIntentReceiver
     */
    public void setMediaButtonReceiver(Class<? extends MediaButtonIntentReceiver> cls) {
        if (cls != null) {
            mediaButtonIntenerReceiverCls = cls;
            Map<String, Object> map = getExtraMap();
            commandFactory.sendCommand(Command.ACTION_MEDIA_BUTTON_RECEIVER, map);
        }
    }

    public static String getCurrentVersionCode() {
        return VERSION_CODE;
    }
}
