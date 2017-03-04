package com.sao.mobile.sao.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.activity.BarDetailActivity;
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.ui.base.BaseService;
import com.squareup.picasso.Picasso;

public class BarNotificationService extends BaseService {
    private static final String TAG = BarNotificationService.class.getSimpleName();

    public static final int BAR_NOTIFICATION_ID = 1000;

    private UserManager mUserManager = UserManager.getInstance();

    public BarNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "on startCommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create");

        Intent intent = new Intent(HomeFragment.UPDATE_CURRENT_BAR);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        startForeground(BAR_NOTIFICATION_ID, getBarNotification(this, mUserManager.currentBar, mUserManager.currentBar.getName(), getString(R.string.notification_welcome), mUserManager.currentBar.getThumbnail()));
    }

    public static Notification getBarNotification(Context context, Bar bar, String barTitle, String contentText, String barThumbnail) {
        Intent notificationIntent = new Intent(context, BarDetailActivity.class);
        notificationIntent.putExtra(BarDetailActivity.BAR_EXTRA, bar);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BarDetailActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =  new Notification.Builder(context)
                .setContentTitle(barTitle)
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentIntent(pendingIntent)
                .build();
        final RemoteViews contentView = notification.contentView;
        Picasso.with(context).load(barThumbnail).into(contentView,  android.R.id.icon, BAR_NOTIFICATION_ID, notification);

        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");
    }
}
