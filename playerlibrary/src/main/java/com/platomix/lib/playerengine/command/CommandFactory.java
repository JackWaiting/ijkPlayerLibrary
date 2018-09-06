package com.platomix.lib.playerengine.command;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.platomix.lib.playerengine.core.local.PlayerService;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 * 发送命令到PlayerService或其子类中
 * 需要调用setTarget()方法设置接收命令的服务
 * @author jackwaiting
 * 
 */
public final class CommandFactory {
	private static CommandFactory factory;
	private Class<? extends PlayerService> target;
	private Context mContext;
	private CommandFactory(Context context){
		mContext = context;
	}



	public static CommandFactory getInstance(Context context) {
		if(factory == null){
			factory = new CommandFactory(context);
		}
		return factory;
	}
	
	public void setTarget(Class<? extends PlayerService> cls) {
		target = cls;
	}
	
	public void sendCommand(String common) {
		Intent intent = getIntent(mContext, common);
		if(intent != null){
			mContext.startService(intent);
		}
	}
	
	public void sendCommand(String common, Map<String, Object> extraMap) {
		Intent intent = getIntent(mContext, common);
		if(intent != null && extraMap != null){
			Set<Entry<String, Object>> set = extraMap.entrySet();
			for (Entry<String, Object> entry : set) {
				String key = entry.getKey();
				Object obj = entry.getValue();
				if (obj instanceof Integer) {
					intent.putExtra(key, (Integer) obj);
				} else if (obj instanceof String) {
					intent.putExtra(key, (String) obj);
				} else if (obj instanceof Boolean) {
					intent.putExtra(key, (Boolean) obj);
				} else if (obj instanceof Float) {
					intent.putExtra(key, (Float) obj);
				}
			}
		}
		if(intent != null){
			mContext.startService(intent);
			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				mContext.startForegroundService(intent);
			} else {

			}*/
		}
	}
	
	/**
	 * 获取创建服务的意图
	 * 
	 * @param action
	 * @return
	 */
	private Intent getIntent(Context context, String action) {
		Intent intent = new Intent(action);
		if(target == null){
			return null;
		}
		intent.setClass(context, target);
		return intent;
	}
	
	public static class Extra{
		/**
		 * 进度 int
		 */
		public static final String EXTRA_SEEK_PERCENT = "percent";
		/**
		 * 选择的歌曲位置 int
		 */
		public static final String EXTRA_SKIP_INDEX = "skip_index";
		/**
		 * 左侧音量 float
		 */
		public static final String EXTRA_LEFT_VOLUME = "left_volume";
		/**
		 * 右侧音量 float
		 */
		public static final String EXTRA_RIGHT_VOLUME = "right_volume";
		/**
		 * 是否关闭通知栏 boolean
		 */
		public static final String EXTRA_CLOSE_NOTIFICATION = "is_close_notifi";
		/**
		 * 发送命令端 
		 */
		public static final String EXTRA_FROM = "from";

		/**
		 * 发送Speed
		 */
		public static final String EXTRA_SPEED = "speed";

		/**
		 * 发送Speed
		 */
		public static final String EXTRA_URL = "url";
	}
	
	public static class Command{
		/**
		 * 播放
		 */
		public static final String ACTION_PLAY = "com.jackwaiting.play";

		/**
		 * 播放
		 */
		public static final String ACTION_REAL_PLAY = "com.jackwaiting.real.play";

		/**
		 * 停止
		 */
		public static final String ACTION_STOP = "com.jackwaiting.stop";
		/**
		 * 暂停
		 */
		public static final String ACTION_PAUSE = "com.jackwaiting.pause";
		/**
		 * 继续
		 */
		public static final String ACTION_RESUME = "com.jackwaiting.resume";
		/**
		 * 设置进度
		 */
		public static final String ACTION_SEEK_TO = "com.jackwaiting.seek_to";
		/**
		 * 设置播放列表
		 */
		public static final String ACTION_PLAY_LIST = "com.jackwaiting.playlist";
		/**
		 * 下一首
		 */
		public static final String ACTION_NEXT = "com.jackwaiting.next";
		/**
		 * 上一首
		 */
		public static final String ACTION_PRE = "com.jackwaiting.pre";
		/**
		 * 绑定监听器
		 */
		public static final String ACTION_BIND_LISTENER = "com.jackwaiting.bind_listener";
		/**
		 * 跳转到
		 */
		public static final String ACTION_SKIP_TO = "com.jackwaiting.skip_to";
		/**
		 * 设置播放模式
		 */
		public static final String ACTION_SET_PLAYMODE = "com.jackwaiting.set_playmode";
		/**
		 * 设置音量
		 */
		public static final String ACTION_SET_VOLUME = "com.jackwaiting.set_volume";
		/**
		 * 播放／暂停切换
		 */
		public static final String ACTION_TOGGLE_PLAY = "com.jackwaiting.toggle";
		/**
		 * 播放／暂停切换
		 */
		public static final String ACTION_DEVICE_TOGGLE_PLAY = "com.jackwaiting.device.toggle";
		/**
		 * 保持屏幕常亮
		 */
		public static final String ACTION_WAKE_MODE = "com.jackwaiting.wake_mode";

		/**
		 * 快进快退
		 */
		public static final String ACTION_SPEED = "com.jackwaiting.speed";


		public static final String ACTION_FADE_VOLUME = "com.jackwaiting.fade_volume";
		
		public static final String ACTION_MEDIA_BUTTON_RECEIVER = "com.jackwaiting.mediabuttonReceiver";
		
		public static final String ACTION_PLAY_NEXT_WHEN_ERROR = "com.jackwaiting.playnext_when_error";
	}
}
