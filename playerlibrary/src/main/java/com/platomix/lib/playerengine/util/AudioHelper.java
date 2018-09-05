package com.platomix.lib.playerengine.util;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.RemoteControlClient;
import android.os.Build.VERSION;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.platomix.lib.playerengine.core.PlayerEngine;
import com.platomix.lib.playerengine.core.local.MediaButtonIntentReceiver;

public class AudioHelper implements OnAudioFocusChangeListener {
	public static final String TAG = "AudioHelper";
	private AudioManager mAudioManager;
	private OnAudioFocusChangeListener mListener;
	private Context context;
	private ComponentName component;
	private PlayerEngine mPlayerEngine;
	private boolean pauseFromUser;
	private TelephonyManager telManager;
	private MobliePhoneStateListener phoneStateListener;
	private BroadcastReceiver mediaButtonIntentReceiver = new MediaButtonIntentReceiver();
	private Class<? extends BroadcastReceiver> cls;

	public AudioHelper(Context ctx, PlayerEngine playerEngine,
			Class<? extends BroadcastReceiver> cls) {
		this(ctx, (OnAudioFocusChangeListener)null, cls);
		mPlayerEngine = playerEngine;
	}

	public AudioHelper(Context ctx, OnAudioFocusChangeListener listener,
			Class<? extends BroadcastReceiver> cls) {
		this(ctx, listener);
		this.cls = cls;
		component = new ComponentName(context.getPackageName(), cls.getName());
		telManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		phoneStateListener = new MobliePhoneStateListener();
		telManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	public AudioHelper(Context ctx, OnAudioFocusChangeListener listener) {
		mListener = listener;
		context = ctx;
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * 是否为用户暂停的
	 * 
	 * @param fromUser 是否为用户暂停的
	 */
	public void setIsUserPause(boolean fromUser) {
		this.pauseFromUser = fromUser;
	}

	private RemoteControlClient mRemoteControlClient;

	private boolean register;

	/**
	 * 请求音乐焦点
	 * 
	 * @return 音乐焦点
	 */
	public boolean requestFocus() {
		boolean success = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
				.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN);
		registerMediaButtonEventReceiver();
		return success;
	}

	/**
	 * 取消音乐焦点
	 * 
	 * @return 音乐焦点
	 */
	public boolean abandonFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
				.abandonAudioFocus(this);
	}

	/**
	 * 注册媒体按钮广播接收
	 */
	@SuppressLint("NewApi")
	public void registerMediaButtonEventReceiver() {
//		registerReceiver();
		mAudioManager.registerMediaButtonEventReceiver(component);
	}

	@SuppressLint("NewApi")
	private void registerRemoteControlClient() {
		if (VERSION.SDK_INT >= 14) {
			if (this.mRemoteControlClient == null) {
				Intent inFilter = new Intent(Intent.ACTION_MEDIA_BUTTON);
				inFilter.setComponent(component);
				PendingIntent mediaPendingIntent = PendingIntent
						.getBroadcast(context, 0, inFilter,
								PendingIntent.FLAG_UPDATE_CURRENT);
				this.mRemoteControlClient = new RemoteControlClient(
						mediaPendingIntent);
				this.mAudioManager
						.registerRemoteControlClient(this.mRemoteControlClient);
			}

			this.mRemoteControlClient
					.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
							| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
							| RemoteControlClient.FLAG_KEY_MEDIA_PLAY
							| RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
							| RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
							| RemoteControlClient.FLAG_KEY_MEDIA_STOP);
		}
	}

	private void registerReceiver() {
		if (!register) {
			register = true;
			IntentFilter inFilter1 = new IntentFilter(
					Intent.ACTION_MEDIA_BUTTON);
			inFilter1.setPriority(10000);
			context.registerReceiver(mediaButtonIntentReceiver, inFilter1);
		}
	}
	
	private void unRegisterReceiver() {
		if (register) {
			register = false;
			context.unregisterReceiver(mediaButtonIntentReceiver);
		}
	}

	/**
	 * 取消注册媒体按钮广播接收
	 */
	@SuppressLint("NewApi")
	public void unRegisterMediaButtonEventReceiver() {
		unRegisterReceiver();
		if (VERSION.SDK_INT >= 14) {
			// mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
		}
		mAudioManager.unregisterMediaButtonEventReceiver(component);
	}

	public void destroy() {
		if (telManager != null) {
			telManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		unRegisterMediaButtonEventReceiver();
		abandonFocus();
	}

	private class MobliePhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (mPlayerEngine != null && !mPlayerEngine.isLocalPlaying()
						&& !pauseFromUser) {
					mPlayerEngine.resume();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d(TAG, "CALL_STATE_OFFHOOK");
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d(TAG, "CALL_STATE_RINGING");
				if (mPlayerEngine != null && mPlayerEngine.isLocalPlaying()) {
					mPlayerEngine.pauseLocal();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		if (mListener != null) {
			mListener.onAudioFocusChange(focusChange);
		}

		if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			unRegisterMediaButtonEventReceiver();
			// abandonFocus();
		}

		if (mPlayerEngine == null) {
			return;
		}
		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:
			// resume playback
			Log.d(TAG, "AUDIOFOCUS_GAIN");
			if (!mPlayerEngine.isLocalPlaying() && !pauseFromUser) {
				mPlayerEngine.resume();
			}
			registerMediaButtonEventReceiver();
			mPlayerEngine.setVolume(1.0f, 1.0f);
			break;

		case AudioManager.AUDIOFOCUS_LOSS:
			// Lost focus for an unbounded amount of time: stop playback and
			// release media player
			Log.d(TAG, "AUDIOFOCUS_LOSS");
			if (mPlayerEngine.isLocalPlaying() && !pauseFromUser) {
				mPlayerEngine.pauseLocal();
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			// Lost focus for a short time, but we have to stop
			// playback. We don't release the media player because playback
			// is likely to resume
			Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
			if (mPlayerEngine.isLocalPlaying()) {
				mPlayerEngine.pauseLocal();
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			// Lost focus for a short time, but it's ok to keep playing
			// at an attenuated level
			Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
			if (mPlayerEngine.isLocalPlaying()) {
				mPlayerEngine.setVolume(0.1f, 0.1f);
			}
			break;
		}
	}
}
