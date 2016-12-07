package com.example.ssandoy.s236305_mappe3;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;

public class AlarmService extends IntentService {

    private static final String NAME = "AlarmService";


    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";


    private IntentFilter matcher;

    public AlarmService() {
        super(NAME);

        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String alarmId = intent.getStringExtra(PlannedEvent.KEY_PLANID);

        if (matcher.matchAction(action)) {

            if (CREATE.equals(action)) {
                performService(CREATE, alarmId);
            }

            if (CANCEL.equals(action)) {
                performService(CANCEL, alarmId);
                DBHandler.DatabaseHelper.delete(PracticApp.db);
            }


        }
    }

    private void performService(String action, String alarmId) {
        Intent intent;
        PendingIntent pintent;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Cursor curr;

        String eventId = alarmId != null ?  alarmId : null; //setter alarmID til første mulige argument

        if(eventId != null) {
            curr = PracticApp.db.query(PlannedEvent.TABLE_PLANS, null, PlannedEvent.KEY_PLANID+" = ?", new String[]{eventId}, null, null, null);
        } else {
            return;
        }

        if (curr.moveToFirst()) {
            long now = System.currentTimeMillis();
            long time, diff;

            do {
                intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra(PlannedEvent.KEY_PLANID, curr.getLong(curr.getColumnIndex(PlannedEvent.KEY_PLANID)));

                pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); //GJØR DET MULIG Å BÅDE SLETTE, OPPDATERE OG UTFØRE

                time = curr.getLong(curr.getColumnIndex(PlannedEvent.KEY_DATETIME)) - curr.getLong(curr.getColumnIndex(PlannedEvent.KEY_ALERTTIME));
                diff = time-now + (long)(60 * 1000.0); //Ett minutt

                if (CREATE.equals(action)) {
                    if (diff > 0 && diff < (60*1000*60*24*365)) //Ett år
                        am.set(AlarmManager.RTC_WAKEUP, time, pintent);

                } else if (CANCEL.equals(action)) {
                    am.cancel(pintent);
                    PracticApp.db.delete(PlannedEvent.TABLE_PLANS, PlannedEvent.KEY_PLANID + "='" + eventId + "'", null);
                }
            } while(curr.moveToNext());
        }
        curr.close();

    }

}
