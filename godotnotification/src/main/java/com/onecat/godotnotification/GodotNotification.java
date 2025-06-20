package com.onecat.godotnotification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

@SuppressWarnings({"unused"})
public class GodotNotification extends GodotPlugin {

    Activity activity = null;
    PermissionReceiver permissionReceiver = null;
    private boolean checkPushPermissionOnResume = false;

    public class PermissionReceiver extends BroadcastReceiver  {
        @Override
        public void onReceive(Context context, Intent intent) {
            emitSignal("alarm_permission_status_changed", isExactAlarmPermissionGranted());
        }
    }

    public GodotNotification(Godot godot)
    {
        super(godot);
        activity = getActivity();
        checkPushPermissionOnResume = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionReceiver = new PermissionReceiver();
            activity.registerReceiver(permissionReceiver, new IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED));
        }
    }

    @Override
    public View onMainCreate(Activity activity) {
        checkPushPermissionOnResume = false;
        return null;
    }

    @Override
    public void onMainResume() {
        if (checkPushPermissionOnResume) {
            checkPushPermissionOnResume = false;
            emitSignal("notification_permission_status_changed", isNotificationPermissionGranted());
        }
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {}

    @Override
    public void onMainDestroy() {
        if (permissionReceiver != null) {
            activity.unregisterReceiver(permissionReceiver);
        }
        super.onMainDestroy();
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotNotification";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signalInfoSet = new HashSet<>();
        signalInfoSet.add(new SignalInfo("notification_permission_status_changed", Boolean.class));
        signalInfoSet.add(new SignalInfo("alarm_permission_status_changed", Boolean.class));
        return signalInfoSet;
    }

    private PendingIntent getPendingIntent(String message, String title, int tag) {
        Intent i = new Intent(activity.getApplicationContext(), GodotNotificationReceiver.class);
        i.putExtra("notification_id", tag);
        i.putExtra("message", message);
        i.putExtra("title", title);
        PendingIntent sender = PendingIntent.getBroadcast(activity, tag, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return sender;
    }

    @UsedByGodot
    public boolean isExactAlarmPermissionGranted() {
        if (Build.VERSION.SDK_INT < 31)
        {
            return true;
        }
        AlarmManager am = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        return am.canScheduleExactAlarms();
    }

    @UsedByGodot
    public void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < 31)
        {
            return;
        }
        if (isExactAlarmPermissionGranted()) {
            return;
        }
        String packageName = activity.getPackageName();
        Intent requestIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        requestIntent.setData(Uri.parse("package:".concat(packageName)));
        activity.startActivity(requestIntent);
    }

    @UsedByGodot
    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT < 24)
        {
            return true;
        }
        NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        return nm.areNotificationsEnabled();
    }

    @UsedByGodot
    public void requestPushNotificationsPermission() {
        if (isNotificationPermissionGranted()) {
            return;
        }
        checkPushPermissionOnResume = true;
        if (Build.VERSION.SDK_INT >= 26) {
            Intent requestIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            requestIntent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
            activity.startActivity(requestIntent);
        }
    }

    @UsedByGodot
    public void scheduleNotification(String message, String title, int interval, int tag) {
        if(interval <= 0) {
            Log.e("GodotNotification", "Can't schedule a notification with interval lower than 1");
            return;
        }
        PendingIntent sender = getPendingIntent(message, title, tag);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, interval);
        AlarmManager am = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        Log.i("GodotNotification", "Scheduling notification with tag "+Integer.toString(tag)+", interval "+Integer.toString(interval));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    @UsedByGodot
    public void scheduleNotificationExact(String message, String title, int interval, int tag) {
        if(interval <= 0) {
            Log.e("GodotNotification", "Can't schedule a notification with interval lower than 1");
            return;
        }
        PendingIntent sender = getPendingIntent(message, title, tag);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, interval);
        AlarmManager am = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (am.canScheduleExactAlarms()){
                Log.i("GodotNotification", "Scheduling exact notification with tag "+Integer.toString(tag)+", interval "+Integer.toString(interval));
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            }
            else {
                Log.i("GodotNotification", "App can't schedule exact alarms");
            }
        }
        else {
            Log.i("GodotNotification", "Scheduling exact notification with tag "+Integer.toString(tag)+", interval "+Integer.toString(interval));
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    @UsedByGodot
    public void cancelNotification(int tag) {
        AlarmManager am = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        PendingIntent sender = getPendingIntent("", "", tag);
        am.cancel(sender);
    }
}