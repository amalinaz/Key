package com.example.keyapp.Helper;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;public class NotificationHelper {
    public static void saveNotificationToFirestore(
            String receiverId,
            String receiverRole,
            String title,
            String message,
            String type,
            String orderId
    ) {
        Map<String, Object> notif = new HashMap<>();
        notif.put("receiverId", receiverId);
        notif.put("receiverRole", receiverRole);
        notif.put("title", title);
        notif.put("message", message);
        notif.put("type", type);
        notif.put("orderId", orderId);
        notif.put("read", false);
        notif.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("notifications")
                .add(notif);
    }
}
