package com.sao.mobile.sao.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.FriendBarResponse;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.SaoNotificationManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.NotificationConstants;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.Order;

import java.util.Map;

public class SaoMessagingService extends FirebaseMessagingService {
    private static final String TAG = SaoMessagingService.class.getSimpleName();

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();
    private SaoNotificationManager notificationManager = SaoNotificationManager.getInstance();

    public SaoMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
            parseNotification(data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Parse data for create a correct notification
     *
     * @param data payload with our key values
     */
    private void parseNotification(Map<String, String> data) {
        String type = data.get(NotificationConstants.KEY_TYPE);

        switch (type)
        {
            case NotificationConstants.TYPE_ORDER_READY:
                Log.i(TAG, "Order ready");
                orderReady(data);
                break;
            case NotificationConstants.TYPE_ORDER_VALIDATE:
                Log.i(TAG, "Order validate");
                orderValidate(data);
                break;
            case NotificationConstants.TYPE_BAR_NEWS:
                Log.i(TAG, "Bar news");
                barNews(data.get(NotificationConstants.KEY_NEWS));
                break;
            case NotificationConstants.TYPE_FRIEND_BAR:
                Log.i(TAG, "Bar news");
                friendBar(data.get(NotificationConstants.KEY_FRIEND));
                break;
        }
    }

    private void orderValidate(Map<String, String> data) {
        mOrderManager.removeOrder();

        Intent intent = new Intent(NotificationConstants.TYPE_ORDER_VALIDATE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        BarNotificationService.notifyBarNotification(getBaseContext(), mUserManager.currentBar, getText(R.string.order_step_validate).toString());
    }

    private void orderReady(Map<String, String> data) {
        mOrderManager.order.setStep(Order.Step.READY);

        Intent intent = new Intent(NotificationConstants.TYPE_ORDER_READY);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        BarNotificationService.notifyBarNotification(getBaseContext(), mUserManager.currentBar, getText(R.string.order_step_ready).toString());
    }

    private void barNews(String newsStr) {
        News news = new Gson().fromJson(newsStr, News.class);
        notificationManager.displayNewsNotification(getApplicationContext(), news);

        Intent intent = new Intent(NotificationConstants.TYPE_BAR_NEWS);
        intent.putExtra(NotificationConstants.TYPE_BAR_NEWS, news);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void friendBar(String friendStr) {
        FriendBarResponse friendBarResponse = new Gson().fromJson(friendStr, FriendBarResponse.class);
        notificationManager.displayFriendBarNotification(getApplicationContext(), friendBarResponse);
    }
}
