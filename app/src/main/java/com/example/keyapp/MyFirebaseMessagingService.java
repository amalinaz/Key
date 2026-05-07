package com.example.keyapp;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String CHANNEL_ID = "firebase_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "";
        String message = "";

        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        }

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        saveNotiftoDb(title,message);

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM_DEBUG", "Notif: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM_DEBUG", "Data: " + remoteMessage.getData().toString());
        }



        sendNotification(title, message);
    }

    private void saveNotiftoDb(String title, String message){
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .add(new HashMap<String, Object>() {{
                    put("title", title);
                    put("message", message);
                    put("receiverId", FirebaseAuth.getInstance().getUid());
                    put("timestamp", System.currentTimeMillis());
                }});
    }
    private void sendNotification(String title, String messageBody) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "FirebaseChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Channel for Firebase notifications");
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true) // biar hilang pas diklik
                .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}