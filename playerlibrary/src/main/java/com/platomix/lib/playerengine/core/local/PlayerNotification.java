package com.platomix.lib.playerengine.core.local;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;


import com.bumptech.glide.request.target.NotificationTarget;
import com.platomix.lib.playerengine.command.CommandFactory.Command;
import com.platomix.lib.playerengine.util.LogManager;
import com.platomix.lib.playerengine.util.ReflectUtil;

class PlayerNotification {
    public static final int ID = 101;
    private Notification notification;
    private static final String NOTIFICATION_LAYOUT_NAME = "platomix_layout_player_notification";
    private static final String NOTIFICATION_BIG_LAYOUT_NAME = "platomix_layout_player_notification_big";
    private static final String BUTTON_NEXT_ID_NAME = "btn_next_music";
    private static final String BUTTON_PRE_ID_NAME = "btn_pre_music";
    private static final String BUTTON_TOGGLE_ID_NAME = "btn_play_pause";
    private static final String BUTTON_DESTORY = "btn_destroy";
    private static final String TV_MUSIC_NAME = "tv_music_name";
    private static final String TV_ARTIST = "tv_artist";
    private static final String IV_MUSIC_COVER = "iv_music_cover";

    private static final String ICON_DRAWABLE = "icon_drawable";
    private static final String DRAWABLE_PLAY_STATE = "btn_notification_player_stop";
    private static final String DRAWABLE_PAUSE_STATE = "btn_notification_player_play";
    private static final String DRAWABLE_MUSIC_COVER = "notification_default_cover_mini";

    private static final int FLAG = PendingIntent.FLAG_UPDATE_CURRENT;

    private Context mContext;
    private String mPackageName;
    private Class<?> layoutClass, idClass, drawableClass;
    private int[] requestCodes = {1, 2, 3, 4};
    private int[] resIds;
    private String[] actions = {Command.ACTION_TOGGLE_PLAY,
            Command.ACTION_NEXT, Command.ACTION_PRE, Command.ACTION_STOP};
    private int playDrawableId, pauseDrawableId, musicCoverDrawableId,
            musicNameTvId, artistNameTvId, musicCoverIv;
    private PlayerService mService;
    private NotificationManager mManager;

    private RemoteViews views;
    private RemoteViews bigContentView;
    private NotificationTarget notificationTarget;

    public PlayerNotification(PlayerService service) {
        mService = service;
        mContext = service.getApplicationContext();
        mPackageName = service.getPackageName();
        mManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        initRes();
    }

    private int layoutId = -1;
    private int biglayoutId = -1;

    private void initRes() {
        layoutClass = ReflectUtil.findClassInR(mPackageName, "layout");
        idClass = ReflectUtil.findClassInR(mPackageName, "id");
        drawableClass = ReflectUtil.findClassInR(mPackageName, "drawable");
        layoutId = ReflectUtil.getIntInClassR(mContext, layoutClass,
                NOTIFICATION_LAYOUT_NAME);
        biglayoutId = ReflectUtil.getIntInClassR(mContext, layoutClass,
                NOTIFICATION_BIG_LAYOUT_NAME);

        musicCoverIv = findViewIdByIdName(IV_MUSIC_COVER);

        musicCoverDrawableId = findDrawableIdByIdName(DRAWABLE_MUSIC_COVER);
        playDrawableId = findDrawableIdByIdName(DRAWABLE_PLAY_STATE);
        pauseDrawableId = findDrawableIdByIdName(DRAWABLE_PAUSE_STATE);

        musicNameTvId = findViewIdByIdName(TV_MUSIC_NAME);
        artistNameTvId = findViewIdByIdName(TV_ARTIST);
        resIds = new int[4];
        resIds[0] = findViewIdByIdName(BUTTON_TOGGLE_ID_NAME);
        resIds[1] = findViewIdByIdName(BUTTON_NEXT_ID_NAME);
        resIds[2] = findViewIdByIdName(BUTTON_PRE_ID_NAME);
        resIds[3] = findViewIdByIdName(BUTTON_DESTORY);

    }

    private int findIdByIdName(Class<?> clz, String idName) {
        return ReflectUtil.getIntInClassR(mContext, clz, idName);
    }

    private int findViewIdByIdName(String idNmae) {
        return findIdByIdName(idClass, idNmae);
    }

    private int findDrawableIdByIdName(String drawableName) {
        return findIdByIdName(drawableClass, drawableName);
    }


    /**
     * 初始化通知栏
     *
     * @param playing
     * @param musicName
     * @param artist
     */
    public Notification init(boolean playing, String musicName, String artist) {
        if (layoutId < 0) {
            return null;
        }

        views = initContentView(layoutId);
        bigContentView = initContentView(biglayoutId);
        update(playing, musicName, artist);
        int iconId = findDrawableIdByIdName(ICON_DRAWABLE);
        notification = buildNotification(musicName, iconId);
        notificationTarget = new NotificationTarget(mContext, views, musicCoverIv, notification, ID);
        if (bigContentView != null) {
            notificationTarget = new NotificationTarget(mContext, bigContentView, musicCoverIv, notification, ID);
        }

        mService.startForeground(PlayerNotification.ID, notification);
        mManager.notify(ID, notification);
        return notification;
    }

    private RemoteViews initContentView(int layoutId) {
        RemoteViews views = new RemoteViews(mPackageName, layoutId);
        initPendingIntents(views);
        return views;
    }

    private void initPendingIntents(RemoteViews views) {
        for (int i = 0; i < 4; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                    requestCodes[i], new Intent(actions[i]), FLAG);
            views.setOnClickPendingIntent(resIds[i], pendingIntent);
        }
    }

    @SuppressLint("NewApi")
    private void forNewApi(Notification notification) {
        if (notification != null) {
            if (bigContentView != null) {
                notification.bigContentView = bigContentView;
            }
            setMaxPriority();
        }
    }

    @SuppressLint("NewApi")
    private void setMaxPriority() {
        if (notification != null) {
            notification.priority = Notification.PRIORITY_MAX;
        }
    }

    private Notification buildNotification(String musicName, int iconId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
        Notification notification =
                mBuilder
                        .setContentTitle(musicName)
                        .setSmallIcon(iconId) // 要设置SmallIcon否则不显示
                        .setContent(views)
                        .setContentIntent(buildContentIntent())
                        .build();
        forNewApi(notification);
        return notification;
    }

    /**
     * notification点击事件
     *
     * @return
     */
    private PendingIntent buildContentIntent() {
        Intent intent = new Intent(mContext, BringToFrontReceiver.class);
        intent.setAction(BringToFrontReceiver.ACTION_BRING_TO_FRONT);
        return PendingIntent.getBroadcast(mContext.getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 更新通知栏
     *
     * @param playing
     * @param musicName
     * @param artist
     */
    public void update(boolean playing, String musicName, String artist) {
        if (notification != null) {
            setMaxPriority();
            update(views, playing, musicName, artist);
            update(bigContentView, playing, musicName, artist);
        }
    }

    /**
     * 更新通知栏
     *
     * @param musicName
     * @param artist
     */
    public void updateText(String musicName, String artist) {
        if (notification != null) {
            setMaxPriority();
            updateText(views, musicName, artist);
            updateText(bigContentView, musicName, artist);
        }
    }

    /**
     * 更新通知栏播放按钮状态
     *
     * @param playing
     */
    public void update(boolean playing) {
        if (notification != null) {
            setMaxPriority();
            views.setImageViewResource(resIds[0], playing ? playDrawableId
                    : pauseDrawableId);
            if (bigContentView != null) {
                bigContentView.setImageViewResource(resIds[0], playing ? playDrawableId
                        : pauseDrawableId);
            }
            mManager.notify(ID, notification);
        }
    }

    /**
     * 更新通知栏
     *
     * @param playing
     * @param musicName
     * @param artist
     */
    private void update(RemoteViews views, boolean playing, String musicName, String artist) {
        if (notification != null && views != null) {
            views.setImageViewResource(resIds[0], playing ? playDrawableId
                    : pauseDrawableId);
            views.setImageViewResource(musicCoverIv, musicCoverDrawableId);
            updateText(views, musicName, artist);
            mManager.notify(ID, notification);
        }
    }

    /**
     * 更新通知栏
     *
     * @param musicName
     * @param artist
     */
    private void updateText(RemoteViews views, String musicName, String artist) {
        if (notification != null && views != null) {
            views.setTextViewText(musicNameTvId, musicName);
            views.setTextViewText(artistNameTvId, artist);
            mManager.notify(ID, notification);
        }
    }


    private void updateImage() {
        if (notification != null) {
            views.setImageViewResource(musicCoverIv, musicCoverDrawableId);
            if (bigContentView != null) {
                bigContentView.setImageViewResource(musicCoverIv, musicCoverDrawableId);
            }
            mManager.notify(ID, notification);
        }
    }

    private String currentImageUri = "";

    public String getCurrentImageUri() {
        return currentImageUri;
    }

    /**
     * 更新图片
     *
     * @param bitmap
     */
    public void updateImage(String imageUri, Bitmap bitmap) {

        LogManager.e("看看这个", "url = " + imageUri);
        currentImageUri = imageUri;
        if (notification != null) {

            /*Glide.with(mContext).load(imageUri).asBitmap().placeholder(R.drawable.icon_drawable).error(R.drawable.icon_drawable).listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    return true;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }) .dontAnimate().into(notificationTarget);*/

            //Glide.with(mContext).load(currentImageUri).asBitmap().into(notificationTarget);

            if (bitmap != null) {
                views.setImageViewBitmap(musicCoverIv, bitmap);
                if (bigContentView != null) {
                    bigContentView.setImageViewBitmap(musicCoverIv, bitmap);
                }
            } else {
                updateImage();
            }
            mManager.notify(ID, notification);
        }
    }


    /**
     * 关闭通知栏
     */
    public void close() {
        mManager.cancel(ID);
    }

}
