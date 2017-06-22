package com.sao.mobile.sao.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.activity.BarActivity;
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.ui.base.BaseService;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class BarNotificationService extends BaseService {
    public static final int BAR_NOTIFICATION_ID = 1000;
    private static final String TAG = BarNotificationService.class.getSimpleName();
    private static Notification.Builder notificationBuilder;

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    public BarNotificationService() {
    }

    public static void notifyBarNotification(Context context, Bar bar, String contentText) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setContentText(contentText);

        notificationManager.notify(
                BarNotificationService.BAR_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static Notification getBarNotification(Context context, Bar bar, String contentText, Bitmap barBitmap) {
        if (bar == null) {
            return null;
        }

        Intent notificationIntent = new Intent(context, BarActivity.class);
        notificationIntent.putExtra(BarActivity.BAR_EXTRA, bar);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BarActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new Notification.Builder(context)
                .setContentTitle(bar.getName())
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(barBitmap)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentIntent(pendingIntent);
        return notificationBuilder.build();
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

        if (mUserManager.currentBar == null) {
            stopSelf();
            return;
        }

        Intent intent = new Intent(HomeFragment.UPDATE_CURRENT_BAR);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        new DownloadImageTask(mContext, mUserManager.currentBar.getThumbnail()).execute();
    }

    private void startNotification(Bitmap bitmap) {
        String orderStatus = "";

        if (mOrderManager.isProductOk() && mOrderManager.order.getStep().equals(Order.Step.INPROGRESS)) {
            orderStatus = getString(R.string.order_step_in_progress);
        } else if (mOrderManager.isProductOk() && mOrderManager.order.getStep().equals(Order.Step.READY)) {
            orderStatus = getString(R.string.order_step_ready);
        } else {
            orderStatus = getString(R.string.notification_welcome);
        }

        Notification notification = getBarNotification(mContext, mUserManager.currentBar, orderStatus, bitmap);
        if (notification == null) {
            return;
        }
        startForeground(BAR_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context mContext;
        private String mThumbnail;

        public DownloadImageTask(Context context, String thumbnail) {
            this.mContext = context;
            this.mThumbnail = thumbnail;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
                bitmap = Picasso.with(mContext).load(mThumbnail).resize(avatarSize, avatarSize).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ((BarNotificationService) mContext).startNotification(bitmap);
        }
    }
}
