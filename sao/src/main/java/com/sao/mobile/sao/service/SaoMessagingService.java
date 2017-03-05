package com.sao.mobile.sao.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.entities.Order;

import java.util.Map;

public class SaoMessagingService extends FirebaseMessagingService {
    private static final String TAG = SaoMessagingService.class.getSimpleName();

    public static final String KEY_TYPE = "type";

    public static final String TYPE_OPEN_ORDER = "openOrder";
    public static final String TYPE_ORDER_VALIDATE = "orderValidate";
    public static final String TYPE_ORDER_READY = "orderReady";
    public static final String TYPE_ORDER_INPROGRESS = "orderInprogress";

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();

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
        String type = data.get(KEY_TYPE);

        switch (type)
        {
            case TYPE_ORDER_READY:
                orderReady(data);
                break;
            case TYPE_ORDER_VALIDATE:
                orderValidate(data);
                break;
        }
    }

    private void orderValidate(Map<String, String> data) {
        mOrderManager.removeOrder();

        Intent intent = new Intent(TYPE_ORDER_VALIDATE);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        BarNotificationService.notifyBarNotification(getBaseContext(), mUserManager.currentBar, getText(R.string.order_step_validate).toString());
    }

    private void orderReady(Map<String, String> data) {
        mOrderManager.order.setStep(Order.Step.READY);

        Intent intent = new Intent(TYPE_ORDER_READY);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        BarNotificationService.notifyBarNotification(getBaseContext(), mUserManager.currentBar, getText(R.string.order_step_ready).toString());
    }
}
