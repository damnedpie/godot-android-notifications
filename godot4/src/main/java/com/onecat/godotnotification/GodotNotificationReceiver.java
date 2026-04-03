package com.onecat.godotnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.media.RingtoneManager;

public class GodotNotificationReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notification_id", 0);
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        Log.i("GodotNotification", "Receive notification: "+message);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "GODOT_NOTIFICATIONS" , importance) ;
            notificationChannel.setShowBadge(true);
            //builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            manager.createNotificationChannel(notificationChannel) ;
        }

        Class appClass = null;
        try {
            appClass = Class.forName("com.godot.game.GodotApp");
        } catch (ClassNotFoundException e) {
            // app not found, do nothing
            return;
        }

        Intent tapIntent = new Intent(context, appClass);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int iconID = context.getResources().getIdentifier("icon", "mipmap", context.getPackageName());
        int notificationIconID = context.getResources().getIdentifier("notification_icon", "mipmap", context.getPackageName());
        int colorID = context.getResources().getIdentifier("notification_color", "color", context.getPackageName());

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), iconID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setShowWhen(true);
        builder.setContentTitle(title);
        builder.setContentText(message);

        if (notificationIconID <= 0)
            builder.setSmallIcon(iconID);
        else
            builder.setSmallIcon(notificationIconID);
        builder.setLargeIcon(largeIcon);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
        builder.setTicker(message);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setColorized(true);
        if (colorID <= 0)
            builder.setColor(Color.RED);
        else
            builder.setColor(context.getResources().getColor(colorID));
        builder.setContentIntent(pendingIntent);
        builder.setNumber(1);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = builder.build();
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        manager.notify(notificationId, notification);
    }

}