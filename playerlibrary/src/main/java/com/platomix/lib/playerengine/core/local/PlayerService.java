package com.platomix.lib.playerengine.core.local;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;


import com.platomix.lib.playerengine.api.PlaybackMode;
import com.platomix.lib.playerengine.api.Playlist;
import com.platomix.lib.playerengine.command.CommandFactory.Command;
import com.platomix.lib.playerengine.command.CommandFactory.Extra;
import com.platomix.lib.playerengine.core.PlayerListener;
import com.platomix.lib.playerengine.core.local.NotificationAdapter.ImageLoadListener;
import com.platomix.lib.playerengine.core.local.PlayerEngineImpl.OnPlayStateChangeListener;
import com.platomix.lib.playerengine.util.AudioHelper;
import com.platomix.lib.playerengine.util.LogManager;

/**
 * 后台播放服务
 *
 * @author jackwaiting
 */
public class PlayerService extends Service implements OnPlayStateChangeListener {
    private static boolean created, destroy;
    private static PlayerEngineImpl playerEngine;

    private LocalPlayer player;
    private AudioHelper focusHelper;

    private PlayerNotification notifacation;
    private boolean showNotification;

    /**
     * 是否时用户触发的暂停操作
     */
    private boolean isUserPause = true;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        created = true;
        initPlayer();
        LogManager.e("PlayerService", "local PlayerService oncreate()");
    }


    private void initPlayer() {
        player = LocalPlayer.getInstance();
        if (player != null) {
            playerEngine = new PlayerEngineImpl(this);
            playerEngine.setPlayStateChangedListener(this);
            setListener();
            setPlaylist();
            player.setPlayerEngine(playerEngine);
            initAudioHelper();
            focusHelper.requestFocus();
        }
    }

    private void initAudioHelper() {
        if (focusHelper != null) {
            focusHelper.destroy();
        }
        focusHelper = new AudioHelper(getApplicationContext(), playerEngine, LocalPlayer.mediaButtonIntenerReceiverCls);
    }

    /**
     * 设置播放监听
     */
    private void setListener() {
        PlayerListener remoteListener = player.getListener();
        playerEngine.setListener(remoteListener);
    }

    /**
     * 设置播放列表
     */
    private void setPlaylist() {
        // 获取上次的模式
        PlaybackMode mode = player.getPlaybackMode();
        Playlist playList = player.getPlaylist();
        playerEngine.setPlaylist(playList);
        // 恢复上次的模式
        if (mode != null) {
            playerEngine.setPlaybackMode(mode);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        String from = intent.getStringExtra(Extra.EXTRA_FROM);
        LogManager.d("PlayerService", "from " + from);
        LogManager.d("PlayerService", "local " + action);
        if (player == null && MediaButtonIntentReceiver.TAG.equals(from)) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (action != null && player == null) {
            initPlayer();
        }
        if (playerEngine == null || player == null || action == null) {
            return super.onStartCommand(intent, flags, startId);
        } else if (Command.ACTION_MEDIA_BUTTON_RECEIVER.equals(action)) {
            initAudioHelper();
            focusHelper.requestFocus();
        } else if (Command.ACTION_STOP.equals(action)) {
            boolean close = intent.getBooleanExtra(Extra.EXTRA_CLOSE_NOTIFICATION,
                    false);
            if (close) {
                stopSelf();
            } else {
                playerEngine.stop();
            }
            return super.onStartCommand(intent, flags, startId);
        } else if (Command.ACTION_BIND_LISTENER.equals(action)) {
            setListener();
        } else if (Command.ACTION_PLAY_LIST.equals(action)) {
            setPlaylist();
        } else if (Command.ACTION_PLAY.equals(action)) {
            playerEngine.play();
            focusHelper.requestFocus();
        } else if (Command.ACTION_PAUSE.equals(action)) {
            isUserPause = true;
            playerEngine.pauseLocal();
        } else if (Command.ACTION_RESUME.equals(action)) {
            isUserPause = false;
            playerEngine.resume();
            focusHelper.requestFocus();
        } else if (Command.ACTION_SEEK_TO.equals(action)) {
            int percent = intent.getIntExtra(Extra.EXTRA_SEEK_PERCENT, 0);
            playerEngine.seekTo(percent);
        } else if (Command.ACTION_NEXT.equals(action)) {
            playerEngine.next();
            focusHelper.requestFocus();
            isUserPause = false;
        } else if (Command.ACTION_PRE.equals(action)) {
            playerEngine.prev();
            focusHelper.requestFocus();
            isUserPause = false;
        } else if (Command.ACTION_REAL_PLAY.equals(action)) {
            String url = intent.getStringExtra(Extra.EXTRA_URL);
            playerEngine.setRealPlay(url);
        } else if (Command.ACTION_SKIP_TO.equals(action)) {
            int index = intent.getIntExtra(Extra.EXTRA_SKIP_INDEX, 0);
            playerEngine.skipTo(index);
            focusHelper.requestFocus();
            isUserPause = false;
        } else if (Command.ACTION_SET_PLAYMODE.equals(action)) {
            PlaybackMode playmode = player.getPlaybackMode();
            playerEngine.setPlaybackMode(playmode);
        } else if (Command.ACTION_SET_VOLUME.equals(action)) {
            float leftVolume = intent.getFloatExtra(Extra.EXTRA_LEFT_VOLUME, 1);
            float rightVolume = intent.getFloatExtra(Extra.EXTRA_RIGHT_VOLUME, 1);
            playerEngine.setVolume(leftVolume, rightVolume);
        } else if (Command.ACTION_TOGGLE_PLAY.equals(action)) {
            isUserPause = !playerEngine.toggle();
            LogManager.i("", "我开始播放暂停" + isUserPause + "------");
            focusHelper.requestFocus();
        } else if (Command.ACTION_DEVICE_TOGGLE_PLAY.equals(action)) {
            mNotificationAdapter.setNotificationSongToggle();
        } else if (Command.ACTION_WAKE_MODE.equals(action)) {
            playerEngine.setWakeMode();
        } else if (Command.ACTION_FADE_VOLUME.equals(action)) {
            playerEngine.setFadeVolumeWhenStartOrPause(player
                    .isFadeVolumeWhenStartOrPause());
        } else if (Command.ACTION_PLAY_NEXT_WHEN_ERROR.equals(action)) {
            playerEngine.setPlayNextWhenError(player.isPlaynextWhenError());
        } else if (Command.ACTION_SPEED.equals(action)) {
            float speed = intent.getFloatExtra(Extra.EXTRA_SPEED, 1);
            LogManager.i("", "我改变了倍数" + speed + "------");
            playerEngine.setSpeed(speed);
        }
        focusHelper.setIsUserPause(isUserPause);
        boolean isShowNotificationAction = isShowNotificationAction(action);
        if (!showNotification && isShowNotificationAction) {
            initNotification();
        }
        /*if (isShowNotificationAction) {
            updateNotification(action);
        }*/
        return START_NOT_STICKY;
    }

    /**
     * 此操作是否要显示通知栏
     *
     * @param action 相应操作
     */
    private boolean isShowNotificationAction(String action) {
        return Command.ACTION_NEXT.equals(action) || Command.ACTION_PRE.equals(action)
                || Command.ACTION_PLAY.equals(action) || Command.ACTION_RESUME.equals(action)
                || Command.ACTION_TOGGLE_PLAY.equals(action)
                || Command.ACTION_SKIP_TO.equals(action);
    }

    public static final int getCurrentPlayPosition() {
        if (playerEngine == null) {
            return 0;
        }
        return playerEngine.getCurrentPlayPercent();
    }

    public class NoisyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(
                    android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                // 停止播放
                if (playerEngine != null && PlayerEngineImpl.isPlay()) {
                    playerEngine.pauseLocal();
                }
            }
        }
    }

    /**
     * 初始化通知栏
     */
    private void initNotification() {

        NotificationAdapter adapter = onNotificationUpdate();
        if (adapter != null) {
            LogManager.e("PlayerService", "initNotification");
            showNotification = true;
            notifacation = new PlayerNotification(this);
            registerNotificationBroadcastReceiver();
            String musicName = adapter.getMusicName();
            String artistName = adapter.getArtistName();
            notifacation.init(adapter.isMusicPlaying(), musicName, artistName);
        }
    }

    private boolean register;

    private void registerNotificationBroadcastReceiver() {
        register = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Command.ACTION_NEXT);
        intentFilter.addAction(Command.ACTION_PRE);
        intentFilter.addAction(Command.ACTION_TOGGLE_PLAY);
        intentFilter.addAction(Command.ACTION_STOP);
        this.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean interUpt = onNotificationButtonClick(action);

            if (!interUpt) {
                if (mNotificationAdapter != null) {
                    if (Command.ACTION_NEXT.equals(action)) {
                        /*playerEngine.next();
                        focusHelper.requestFocus();*/
                        mNotificationAdapter.setNotificationSongNext();
                    } else if (Command.ACTION_PRE.equals(action)) {
                        /*playerEngine.prev();
                        focusHelper.requestFocus();*/
                        mNotificationAdapter.setNotificationSongPre();
                    } else if (Command.ACTION_TOGGLE_PLAY.equals(action)) {
                        /*playerEngine.toggle();
                        focusHelper.requestFocus();*/
                        mNotificationAdapter.setNotificationSongToggle();
                    } else if (Command.ACTION_STOP.equals(action)) {
                        stopSelf();
                        return;
                    }
                }

            }
        }
    };

    private void updateNotification(String action) {
        NotificationAdapter adapter = onNotificationUpdate();
        if (adapter != null && notifacation != null) {
            String musicName = adapter.getMusicName();
            String artistName = adapter.getArtistName();

            if (!TextUtils.equals(action, Command.ACTION_TOGGLE_PLAY)
                    || TextUtils.isEmpty(notifacation.getCurrentImageUri())) {
                notifacation.updateText(musicName, artistName);
                notifacation.update(adapter.isMusicPlaying());
                adapter.loadMusicImage(new ImageLoadListener() {
                    @Override
                    public void onImageLoaded(String imageUri, Bitmap bitmap) {
                        if (notifacation != null) {
                            notifacation.updateImage(imageUri, bitmap);
                        }
                    }
                });
            }
        }
    }


    /**
     * 取消通知栏
     */
    private void dismissNotification() {
        showNotification = false;
        if (notifacation != null) {
            notifacation.close();
            notifacation = null;
        }
        stopForeground(true);
    }

    /**
     * 点击通知栏里的按钮
     *
     * @param action 可能取值
     * @return boolean 是否拦截事件
     */
    protected boolean onNotificationButtonClick(String action) {
        return false;
    }

    /**
     * 通知栏更新
     *
     * @return NotificationAdatper 从中获取歌曲信息，返回null则不显示通知栏
     */
    protected NotificationAdapter onNotificationUpdate() {

        return mNotificationAdapter;
    }

    private static NotificationAdapter mNotificationAdapter;


    public static void setNotificationAdapter(NotificationAdapter notificationAdapter) {
        mNotificationAdapter = notificationAdapter;
    }


    @Override
    public final void onPlayStateChange(boolean playing) {
        updateNotification("change");
    }

    @Override
    public void onDestroy() {
        if (register) {
            unregisterReceiver(mBroadcastReceiver);
        }
        created = false;
        destroy = true;
        if (focusHelper != null) {
            focusHelper.destroy();
        }
        dismissNotification();
        player = null;
        if (playerEngine != null) {
            playerEngine.stop();
            playerEngine = null;
        }
        super.onDestroy();
    }

}
