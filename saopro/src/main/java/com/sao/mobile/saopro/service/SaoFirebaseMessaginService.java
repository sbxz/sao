package com.sao.mobile.saopro.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sao.mobile.saolib.utils.Utils;
import com.sao.mobile.saopro.ui.MainActivity;
import com.sao.mobile.saopro.ui.fragment.OrderListFragment;

import java.io.Serializable;
import java.util.Map;

public class SaoFirebaseMessaginService extends FirebaseMessagingService {
    private static final String TAG = SaoFirebaseMessaginService.class.getSimpleName();

    public SaoFirebaseMessaginService() {
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
