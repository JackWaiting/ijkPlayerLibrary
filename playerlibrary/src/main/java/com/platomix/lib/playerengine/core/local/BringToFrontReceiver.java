package com.platomix.lib.playerengine.core.local;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
/**
 * 接收Notification点击事件的广播
 * 接收到广播后将应用程序带到前台
 * @author jackwaiting
 *
 */
public final class BringToFrontReceiver extends BroadcastReceiver {
	public static final String ACTION_BRING_TO_FRONT = "jackwaiting.action.BringToFront";

	private static OnBringToFrontReceiverListener onBringToFrontReceiverListener;

	private interface OnBringToFrontReceiverListener{
	    void onBringToFrontReceiverListener();
    }

    public static void setOnBringToFrontReceiverListener(OnBringToFrontReceiverListener listener){
        onBringToFrontReceiverListener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(TextUtils.equals(intent.getAction(), ACTION_BRING_TO_FRONT)){
			bringToFront(context);
		}
	}
	/**
	 * 将应用程序置到前台
	 * @param context
	 */
	private void bringToFront(Context context) {
		//获取ActivityManager  
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {  
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台  
            if(rti.topActivity.getPackageName().equals(context.getPackageName())) {  
                try {  
                    Intent  resultIntent = new Intent(context, Class.forName(rti.topActivity.getClassName()));  
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);  
                    context.startActivity(resultIntent);  
                }catch (ClassNotFoundException e) {  
                    e.printStackTrace();  
                }  
                return;  
            }  
        }
        if(onBringToFrontReceiverListener != null){
            onBringToFrontReceiverListener.onBringToFrontReceiverListener();
        }
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainActivity
        /*Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(resultIntent);*/

        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.muzen.radioplayer");
        context.startActivity(LaunchIntent);
	}
}