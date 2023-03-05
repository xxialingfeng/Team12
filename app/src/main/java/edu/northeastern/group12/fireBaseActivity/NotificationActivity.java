package edu.northeastern.group12.fireBaseActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationActivity extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "group_A8";
    private static final int NOTIFICATION_ID = 0;

    @SuppressLint("MissingPermission")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String url = remoteMessage.getData().get("url");

        Intent intent;
        if (url != null) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, SendStickerActivity.class);
        }

        // Triggered if the notification is selected
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        // Build notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel();
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this) ;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * set up notification channel
     */
    public void setupNotificationChannel() {
        CharSequence name = "New notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String channelId = "group12_A8";
        String description = "Device to device notification";


        NotificationChannel channel;
        channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
