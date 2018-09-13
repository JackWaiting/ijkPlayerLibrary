package com.platomix.lib.playerengine.core.local;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import com.platomix.lib.playerengine.command.CommandFactory;
import com.platomix.lib.playerengine.command.CommandFactory.Command;
import com.platomix.lib.playerengine.command.CommandFactory.Extra;

/**
 * 接收线控／蓝牙按钮事件的广播接收器
 * 
 * @author jackwaiting
 * 
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {
	public static final String TAG = "MediaButtonIntentReceiver";
	private static final int LONG_PRESS_DELAY = 1000;
	private static long mLastClickTime = 0;
	private static boolean mDown = false;
	//127 暂停 126 播放
	
	private static boolean isDown=false;
	@SuppressLint("LongLogTag")
	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		Log.d(this.getClass().getSimpleName(), "onReceive = " + intentAction);
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
			onReceiveCommand(context, Command.ACTION_PAUSE);
		} else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

			if (event == null) {
				return;
			}

			int keycode = event.getKeyCode();
			int action = event.getAction();
			long eventtime = event.getEventTime();

			String command = null;
			switch (keycode) {
			case KeyEvent.KEYCODE_MEDIA_STOP:
				command = Command.ACTION_STOP;
				break;
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				command = Command.ACTION_TOGGLE_PLAY;
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				command = Command.ACTION_NEXT;
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				command = Command.ACTION_PRE;
				break;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				//command = Command.ACTION_TOGGLE_PLAY;
				command = Command.ACTION_PAUSE;
				break;
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				//command = Command.ACTION_TOGGLE_PLAY;
				command = Command.ACTION_PLAY;
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				Log.d(TAG, "volume_up");
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				Log.d(TAG, "volume_down");
				break;
			}

			if (command != null) {
				Log.e(TAG, "action-->"+action);
				Log.i(TAG, "KeyEvent.ACTION_DOWN");
				Log.i(TAG, "RepeatCount:"+event.getRepeatCount());
				if (event.getRepeatCount() == 0) {
					// 发一个广播出去
					onReceiveCommand(context, command);
				}
//				if (action == KeyEvent.ACTION_DOWN) {
//					Log.i(TAG, "KeyEvent.ACTION_DOWN");
//					Log.i(TAG, "RepeatCount:"+event.getRepeatCount());
//					if (event.getRepeatCount() == 0) {
//						// 发一个广播出去
//						onReceiveCommand(context, command);
//					} 
//				} else if (action == KeyEvent.ACTION_UP ){
//					Log.i(TAG, "KeyEvent.ACTION_UP");
//					 abortBroadcast();
//				}
				
				if (action == KeyEvent.ACTION_DOWN) {
					Log.d(TAG, "action down, keycode = " + keycode);
				
					if (mDown) {
						if ((Command.ACTION_TOGGLE_PLAY.equals(command) || Command.ACTION_PLAY
								.equals(command))
								&& mLastClickTime != 0
								&& eventtime - mLastClickTime > LONG_PRESS_DELAY) {
							
						}
					} else if (event.getRepeatCount() == 0) {
						String comm = command;
						if (keycode == KeyEvent.KEYCODE_HEADSETHOOK
								&& eventtime - mLastClickTime < 300) {
							comm = Command.ACTION_NEXT;
							mLastClickTime = 0;
						} else {
							mLastClickTime = eventtime;
						}
						onReceiveCommand(context, comm);
						mDown = true;
					}
				} else {
					Log.d(TAG, "action up, keycode = " + keycode);
					mDown = false;
					
				}
			}
		}
		if (isOrderedBroadcast()) {
			abortBroadcast();
		}
	}

	protected void onReceiveCommand(Context context, String command) {
    	 CommandFactory commandFactory = CommandFactory.getInstance(context);
    	 Map<String, Object> extraMap = new HashMap<String, Object>();
    	 extraMap.put(Extra.EXTRA_FROM, TAG);
    	 commandFactory.sendCommand(command, extraMap);
	}
}
