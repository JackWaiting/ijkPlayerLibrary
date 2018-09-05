package com.platomix.lib.playerengine.core.local;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;


import com.platomix.lib.playerengine.api.PlaybackMode;
import com.platomix.lib.playerengine.api.Playlist;
import com.platomix.lib.playerengine.core.PlayerEngine;
import com.platomix.lib.playerengine.core.PlayerListener;
import com.platomix.lib.playerengine.util.LogManager;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

/**
 * 播放引擎实现类，播放相关的主要代码都在这里
 *
 * @author jackwaiting
 */
class PlayerEngineImpl implements PlayerEngine, IMediaPlayer.OnBufferingUpdateListener
        , IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener {
    public static final String TAG = "PlayerEngineImpl";
    /**
     * 默认进度更新时间间隔
     */
    private static final int DEF_PROGRESS_UPDATE_INTERVAL = 500;
    /**
     * 进度条更新时间间隔
     */
    private int progressUpdateInterval = DEF_PROGRESS_UPDATE_INTERVAL;

    private static InternalMediaPlayer currentMediaPlayer;
    private PlayerListener playerListener;
    private Handler progressHandler;

    private Playlist playList;
    public static boolean isPlaying;
    private Context mContext;
    private boolean wakeMode;
    private PlaybackMode mPlaybackMode;
    private boolean isFadeVolume;
    private boolean usingOpenSLES = false;
    private String pixelFormat = "";//Auto Select=,RGB 565=fcc-rv16,RGB 888X=fcc-rv32,YV12=fcc-yv12,默认为RGB 888X
    private boolean enableBackgroundPlay = false;
    private boolean enableSurfaceView = true;
    private boolean enableTextureView = false;
    private boolean enableNoView = false;
    private boolean usingAndroidPlayer = false;
    private boolean usingMediaCodec = false;
    private boolean usingMediaCodecAutoRotate = false;
    private int bufferdPercent = 0;
    private IjkMediaPlayer ijkMediaPlayer;

    public PlayerEngineImpl(Context context) {
        mContext = context;
        progressHandler = new Handler();
        initMediaPlayer();
    }

    private void initMediaPlayer() {

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);
        /*ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        if (usingMediaCodec) {
            if (usingMediaCodecAutoRotate) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
            }
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        }
        if (usingOpenSLES) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        }

        if (TextUtils.isEmpty(pixelFormat)) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 30);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 5);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "4096");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");
*/
        //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);

        /*ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);//设置缓冲区为100KB，目前我看来，多缓冲了4秒

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100);*/

        this.ijkMediaPlayer = ijkMediaPlayer;
    }

    private InternalMediaPlayer build() {
        final InternalMediaPlayer player = new InternalMediaPlayer(ijkMediaPlayer);

        if (wakeMode) {
            player.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        }

        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:

                        LogManager.i(TAG, "MEDIA_INFO_BUFFERING_START" + what + "---------" + extra);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (playerListener != null) {
                            playerListener.onTrackBuffering(player.uri, 100);
                        }
                        break;
                }
                return false;
            }
        });
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer arg0) {
                player.preparing = false;
                player.start();
                if (playerListener != null) {
                    playerListener.onTrackStart(currentMediaPlayer.uri);
                }
                if (playStateListener != null) {
                    playStateListener.onPlayStateChange(true);
                }
                // 开始更新进度
                progressHandler.postDelayed(progressRunnable,
                        progressUpdateInterval);
            }
        });
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return player;
    }

    @Override
    public void setPlaylist(Playlist playlist) {
        this.playList = playlist;
        if (this.playList != null && mPlaybackMode != null) {
            this.playList.setPlaylistPlaybackMode(mPlaybackMode);
        }
    }

    @Override
    public Playlist getPlaylist() {
        return playList;
    }

    public int getAudioSessionId() {
        if (currentMediaPlayer != null) {
            return currentMediaPlayer.getAudioSessionId();
        }
        return -1;
    }


    @Override
    public void play() {

        if (playList != null) {
            LogManager.e("LocalPlayListener", "我走了这个onPlay" + playList.getSelectedUri());
            LogManager.e("LocalPlayListener", "当前播放库选中的position" + playList.getSelectedIndex());
            play(playList.getSelectedUri(), false);
        }
    }

    @Override
    public void setPercent(int percent) {

    }

    /**
     * 播放链接
     *
     * @param uri     本地或远程地址
     * @param restart 如果是同一个链接，是否重新播放
     */
    private void play(String uri, boolean restart) {
        LogManager.i(TAG, "我走了这个play");

        if (!TextUtils.isEmpty(uri)) {
            if (currentMediaPlayer != null) {
                if (!uri.equals(currentMediaPlayer.uri) || restart) {
                    cleanUp();
                    currentMediaPlayer = build();
                    onStartBuffer(uri);
                    start(uri);
                } else {
                    resume();
                }
            } else {
                currentMediaPlayer = build();
                onStartBuffer(uri);
                start(uri);
            }
        }
    }

    private void onStartBuffer(String uri) {
        LogManager.i(TAG, "onStartBuffer" + bufferdPercent);
        if (uri.startsWith("http") || uri.startsWith("www") && playerListener != null) {

            if (currentMediaPlayer != null) {
                LogManager.i(TAG, "onStartBuffer currentMediaPlayer.bufferdPercent" + currentMediaPlayer.bufferdPercent);
                bufferdPercent = currentMediaPlayer.bufferdPercent;
            }
            playerListener.onTrackBuffering(uri, bufferdPercent);
        }
    }

    /**
     * 重新播放
     */
    private void restart() {
        if (currentMediaPlayer != null) {
            play(currentMediaPlayer.uri, true);
        }
    }

    /**
     * 开始播放
     *
     * @param uri
     */
    private void start(String uri) {
        currentMediaPlayer.internalPlaylist = playList;
        currentMediaPlayer.uri = uri;
        if (playerListener != null) {
            playerListener.onTrackChanged(uri);
        }
    }

    private void realStart(String uri){
        if (playerListener != null) {
            playerListener.onTrackChanged(uri);
        }
        try {
            LogManager.d("URI", uri);
            //
            //currentMediaPlayer.reset();
            initMediaPlayer();
            currentMediaPlayer.setDataSource(uri);
            currentMediaPlayer.preparing = true;
            currentMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLocalPlaying() {
        if (currentMediaPlayer == null) {
            return false;
        }

        return currentMediaPlayer.isPlaying();
    }


    static boolean isPlay() {
        if (currentMediaPlayer == null) {
            return false;
        }
        return currentMediaPlayer.isPlaying();
    }


    @Override
    public void stop() {
        if (playerListener != null && currentMediaPlayer != null) {
            //playerListener.onTrackProgress(currentMediaPlayer.uri, 0, 0, currentMediaPlayer.getDuration());
            playerListener.onTrackProgress(currentMediaPlayer.uri, 0, 0, 0);
            playerListener.onTrackBuffering(currentMediaPlayer.uri, 1);
        }
        cleanUp();
        if (playerListener != null && currentMediaPlayer != null) {
            playerListener.onTrackStop(currentMediaPlayer.uri);
        }
    }

    @Override
    public void pauseLocal() {
        if (currentMediaPlayer != null) {
            if (currentMediaPlayer.preparing) {
                return;
            }

            if (currentMediaPlayer.isPlaying()) {
                currentMediaPlayer.pause();
                if (playerListener != null) {
                    playerListener.onTrackPause(currentMediaPlayer.uri);
                }
                if (playStateListener != null) {
                    playStateListener.onPlayStateChange(false);
                }
            }
        }
    }


    @Override
    public void resume() {
        if (currentMediaPlayer != null) {
            if (currentMediaPlayer.preparing) {
                return;
            }if (!currentMediaPlayer.isPlaying()) {
                currentMediaPlayer.start();
                //play(playList.getSelectedUri(), false);
                if (playerListener != null) {
                    LogManager.e("LocalPlayListener", "我走了这个onResume" + playList.getSelectedUri());
                    playerListener.onTrackStart(playList.getSelectedUri());
                }
                if (playStateListener != null) {
                    playStateListener.onPlayStateChange(true);
                }
            }
        } else {
            play();
        }
    }

    @Override
    public boolean toggle() {
        if (currentMediaPlayer == null) {
            play();
            return true;
        }
        if (currentMediaPlayer.isPlaying()) {
            pauseLocal();
            return false;
        } else {
            resume();
            return true;
        }
    }

    @Override
    public void seekTo(int percent) {
        if (currentMediaPlayer == null) {
            return;
        }
        LogManager.i(TAG, "seekTo" + percent / 10);
        /*bufferdPercent = percent/10;
        onStartBuffer(currentMediaPlayer.uri);*/
        percent = Math.min(percent, 1000);
        currentMediaPlayer.seekTo(currentMediaPlayer.getDuration() * percent
                / 1000);
    }

    @Override
    public void next() {//TODO 下一曲
        if (playList != null) {
            playList.selectNext();
            if (currentMediaPlayer != null && playList.getSelectedUri().equals(currentMediaPlayer.uri)) {
                restart();
            } else {
                play();
            }
        }
    }

    @Override
    public void prev() {
        if (playList != null) {
            playList.selectPrev();
            play();
        }
    }

    @Override
    public void skipTo(int index) {
        if (playList != null) {
            playList.select(index);
            play();
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void setListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
        if (currentMediaPlayer != null) {
            onStartBuffer(currentMediaPlayer.uri);
        }
    }

    public interface OnPlayStateChangeListener {
        void onPlayStateChange(boolean playing);
    }

    private OnPlayStateChangeListener playStateListener;

    /**
     * 播放状态变化监听
     *
     * @param listener
     */
    void setPlayStateChangedListener(OnPlayStateChangeListener listener) {
        playStateListener = listener;
    }

    @Override
    public void setPlaybackMode(PlaybackMode aMode) {
        mPlaybackMode = aMode;
        if (playList != null) {
            playList.setPlaylistPlaybackMode(aMode);
        }
    }

    @Override
    public PlaybackMode getPlaybackMode() {
        if (playList != null) {
            return playList.getPlaylistPlaybackMode();
        }
        return null;
    }

    @Override
    public void setWakeMode() {
        wakeMode = true;
    }

    /**
     * Stops & destroys media player
     */
    private void cleanUp() {
        progressHandler.removeCallbacks(progressRunnable);
        if (currentMediaPlayer != null) {
            try {
                currentMediaPlayer.stop();
            } catch (IllegalStateException e) {
                // e.printStackTrace();
            } finally {
                //currentMediaPlayer.reset();
                currentMediaPlayer.release();
            }
            currentMediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if (getPlaybackMode() == PlaybackMode.SINGLE_REPEAT) {
            restart();
        } else {
            // 注意：MediaPlayer reset() 也会调用onCompletion方法，所以调用next（）会出现循环调用
            // next();
            // stop()方法或release()方法是否也调用？注意onError()是否会影响此处
            // 加上判断
            if (!currentMediaPlayer.preparing) {
                next();
            }
        }
    }

    /**
     * FAIL_TIME_FRAME起始
     */
    private long mLastFailTime;

    /**
     * FAIL_TIME_FRAME时间内发生的错误数
     */
    private long mTimesFailed;

    /**
     * Time frame - 在此时间内发生错误的次数
     */
    private static final long FAIL_TIME_FRAME = 1000;
    /**
     * 在FAIL_TIME_FRAME内可接受的错误次数
     */
    private static final int ACCEPTABLE_FAIL_NUMBER = 5;


    @Override
    public boolean onError(IMediaPlayer arg0, int what, int extra) {
        // logError(what, extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                LogManager.e(TAG, "unknown media playback error");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                LogManager.e(TAG, "server connection died");
            default:
                LogManager.e(TAG, "generic audio playback error");
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                LogManager.e(TAG, "IO media error");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                LogManager.e(TAG, "media error, malformed");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                LogManager.e(TAG, "unsupported media content");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                LogManager.e(TAG, "media timeout error");
                break;
            case -2147483648:
                LogManager.e(TAG, "文件损坏或不存在！");
                break;
            default:
                LogManager.e(TAG, "unknown playback error");
                break;
        }
        if (what == -10000) {
            what = 1;
        }
        if (playerListener != null) {
            playerListener.onTrackStreamError(currentMediaPlayer.uri, what,
                    extra);
        }

        stop();
        if (!playnextWhenError) {
            return true;
        }
        long failTime = System.currentTimeMillis();
        if (failTime - mLastFailTime > FAIL_TIME_FRAME) {
            // outside time frame
            mTimesFailed = 1;
            mLastFailTime = failTime;
            LogManager.w(TAG, "PlayerEngineImpl " + mTimesFailed
                    + " fail within 1 second");
        } else {
            // inside time frame
            mTimesFailed++;
            if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
                cleanUp();
                LogManager.w(TAG, "连续发生错误，停止播放");
            }
        }

        if (playList != null && !playList.isLastTrackOnList()
                && mTimesFailed <= ACCEPTABLE_FAIL_NUMBER) {
            next();
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer arg0, int percent) {
        LogManager.i(TAG, "onBufferingUpdate" + percent);
        if (currentMediaPlayer != null) {
            currentMediaPlayer.bufferdPercent = percent;
        }
        if (playerListener != null && percent > 0) {
            playerListener.onTrackBuffering(currentMediaPlayer.uri, percent);
        }
    }

    private class InternalMediaPlayer extends MediaPlayerProxy {
        /**
         * 是否正在prepare
         */
        public boolean preparing = false;

        @SuppressWarnings("unused")
        public Playlist internalPlaylist;

        private String uri = "";
        private float currentVolumeValue = 0;
        private boolean pause;
        private InternalMediaPlayer instance;
        public int bufferdPercent;

        public InternalMediaPlayer(IMediaPlayer backEndMediaPlayer) {
            super(backEndMediaPlayer);
            instance = this;

        }

        public void setSpeed(IMediaPlayer backEndMediaPlayer){
            new InternalMediaPlayer(backEndMediaPlayer);

        }


        @Override
        public void pause() {
            pause = true;
            if (isFadeVolume) {
                handler.removeCallbacks(volumeRunnable);
                handler.post(volumeRunnable);
            } else {
                super.pause();
            }
        }

        @Override
        public void start() {
            LogManager.i("看看值", "我执行了外部的start");
            pause = false;
            super.setVolume(currentVolumeValue, currentVolumeValue);
            super.start();
            if (isFadeVolume) {
                handler.removeCallbacks(volumeRunnable);
                currentVolumeValue = Math.max(0, currentVolumeValue);
                handler.post(volumeRunnable);
            } else {
                super.setVolume(1, 1);
            }
        }

        @Override
        public void release() {
            handler.removeCallbacks(volumeRunnable);
            instance = null;
            super.release();
        }

        @Override
        public boolean isPlaying() {
            return super.isPlaying() && !pause;
        }

        private Handler handler = new Handler();
        private Runnable volumeRunnable = new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    if (pause) {
                        currentVolumeValue -= 0.05f;
                    } else {
                        currentVolumeValue += 0.02f;
                    }
                    if (currentVolumeValue >= 0 && currentVolumeValue <= 1) {
                        instance.setVolume(currentVolumeValue,
                                currentVolumeValue);
                        handler.postDelayed(this, 50);
                    } else if (currentVolumeValue < 0) {
                        pause = false;
                        InternalMediaPlayer.super.pause();
                    }
                }
            }
        };

    }

    @Override
    public void setSpeed(float speed) {
        if (currentMediaPlayer != null) {
            LogManager.i(TAG, "我改变了倍数" + speed);
            LogManager.i(TAG, "我改变了倍数");
            cleanUp();
            ijkMediaPlayer.setSpeed(speed);
            currentMediaPlayer = build();
            //currentMediaPlayer.start();

            onStartBuffer(playList.getSelectedUri());
            start(playList.getSelectedUri());

        }
    }

    /*
     * 进度更新
     */
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentMediaPlayer != null) {
                long currentDuration = currentMediaPlayer.getCurrentPosition();
                long duration = currentMediaPlayer.getDuration();
                if (playerListener != null) {
                    playerListener.onTrackProgress(currentMediaPlayer.uri,
                            (long) ((float) currentDuration / duration * 1000),
                            currentDuration, duration);
                }
            }
            progressHandler.postDelayed(this, progressUpdateInterval);
        }
    };

    @Override
    public PlayerListener getListener() {
        return playerListener;
    }

    @Override
    public int getCurrentPlayPercent() {
        if (currentMediaPlayer != null && !currentMediaPlayer.preparing) {
            return (int) ((float) currentMediaPlayer.getCurrentPosition()
                    / currentMediaPlayer.getDuration() * 1000);
        }
        return 0;
    }

    @Override
    public void setFadeVolumeWhenStartOrPause(boolean fade) {
        isFadeVolume = fade;
    }

    @Override
    public boolean isFadeVolumeWhenStartOrPause() {
        return isFadeVolume;
    }

    public final void getText() {
    }

    private boolean playnextWhenError = false;

    /**
     * 发生错误时是否自动切换到下一曲
     */
    public void setPlayNextWhenError(boolean playnextWhenError) {
        this.playnextWhenError = playnextWhenError;
    }

}
