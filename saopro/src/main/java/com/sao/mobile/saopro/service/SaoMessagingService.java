package com.sao.mobile.saopro.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sao.mobile.saopro.ui.fragment.OrderListFragment;

import java.util.Map;

public class SaoMessagingService extends FirebaseMessagingService {
    private static final String TAG = SaoMessagingService.class.getSimpleName();

    public static final String TYPE_OPEN_ORDER = "openOrder";
    public static final String TYPE_ORDER_VALIDATE = "orderValidate";
    public static final String TYPE_ORDER_READY = "orderReady";
    public static final String TYPE_ORDER_INPROGRESS = "orderInprogress";

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
        String type = data.get("type");

        switch (type)
        {
            case OrderListFragment.NEW_ORDER_EXTRA:
                Intent intent = new Intent(OrderListFragment.NEW_ORDER_EXTRA);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                break;
        }
    }
}
