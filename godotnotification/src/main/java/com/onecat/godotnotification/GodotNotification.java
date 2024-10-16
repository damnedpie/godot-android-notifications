package com.onecat.godotnotification;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Calendar;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

@SuppressWarnings({"unused"})
public class GodotNotification extends GodotPlugin {

    public GodotNotification(Godot godot)
    {
        super(godot);
    }

    @Override
    public View onMainCreate(Activity activity) {
        return null;
    }

    @Override
    public void onMainResume() {}

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {}

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotNotification";
    }

    private PendingIntent getPendingIntent(String message, String title, int tag) {
        Intent i = new Intent(getActivity().getApplicationContext(), GodotNotificationReceiver.class);
        i.putExtra("notification_id", tag);
        i.putExtra("message", message);
        i.putExtra("title", title);
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(), tag, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return sender;
    }

    @UsedByGodot
    public void scheduleNotification(String message, String title, int interval, int tag) {
        if(interval <= 0) {
            Log.e("GodotNotification", "Can't schedule a notification with interval lower than 1");
            return;
        }
        Log.d("GodotNotification", "Scheduling notification with tag "+Integer.toString(tag)+", interval "+Integer.toString(interval));
        PendingIntent sender = getPendingIntent(message, title, tag);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, interval);

        AlarmManager am = (AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    @UsedByGodot
    public void cancelNotification(int tag) {
        AlarmManager am = (AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);
        PendingIntent sender = getPendingIntent("", "", tag);
        am.cancel(sender);
    }
}