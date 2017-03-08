package com.sao.mobile.sao.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.FriendBarResponse;
import com.sao.mobile.sao.ui.MainActivity;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.manager.AbstractSaoNotificationManager;

import java.io.Serializable;

/**
 * Created by Seb on 06/03/2017.
 */

public class SaoNotificationManager extends AbstractSaoNotificationManager {
    private static SaoNotificationManager ourInstance = new SaoNotificationManager();

    private SaoNotificationManager() {
        super();
    }

    public static SaoNotificationManager getInstance() {
        return ourInstance;
    }

    public void displayNewsNotification(Context context, News news) {
        new DownloadImageTask(context, news, news.getBar().getThumbnail()).execute();
    }

    private void createNewsNotification(Context context, News news, Bitmap bitmap) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification.Builder builder = getBaseNotification(context, news.getBar().getName(), news.getContent(), bitmap, pendingIntent);
        showNotification(context, news.getBar().getBarId().intValue(), builder.build());
    }

    public void displayFriendBarNotification(Context context, FriendBarResponse friendBarResponse) {
        new DownloadImageTask(context, friendBarResponse, friendBarResponse.getFriend().getThumbnail()).execute();
    }

    private void createFriendJoinBarNotification(Context context, FriendBarResponse friendBarResponse, Bitmap bitmap) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification.Builder builder = getBaseNotification(context, friendBarResponse.getFriend().getName(), "vient d'arriver à " + friendBarResponse.getBar().getName(), bitmap, pendingIntent);
        showNotification(context, Integer.parseInt(friendBarResponse.getFriend().getFacebookUserId()) / 2, builder.build());
    }

    private void showNotification(Context context, int notificationId, Notification notification) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notification);
    }

    private Notification.Builder getBaseNotification(Context context, String title, String content, Bitmap bitmap, PendingIntent pendingIntent) {
        return new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentIntent(pendingIntent);
    }

    @Override
    protected void showNotification(Context context, Serializable object, Bitmap bitmap) {
        if (object instanceof News) {
            createNewsNotification(context, (News) object, bitmap);
        } else if (object instanceof FriendBarResponse) {
            createFriendJoinBarNotification(context, (FriendBarResponse) object, bitmap);
        }
    }
}
