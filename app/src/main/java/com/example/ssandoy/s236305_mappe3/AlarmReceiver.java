package com.example.ssandoy.s236305_mappe3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long eventId = intent.getLongExtra(PlannedEvent.KEY_PLANID, -1);

        long[] pattern = {500,500,500,500,500,500,500,500,500};

        PlannedEvent plannedEvent = new PlannedEvent(eventId);
        plannedEvent.loadEvent(PracticApp.db);
        plannedEvent.setAlarmStatus(PlannedEvent.EXECUTED);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.pxicon);
        icon.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pxicon),100,100,true);
        PendingIntent pintent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        Notification  notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(icon)
                .setContentTitle("PracticApp")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(plannedEvent.getEventName())
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pintent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(pattern)
                .build();

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int)eventId, notification);
        plannedEvent.update(PracticApp.db);

    }
}
