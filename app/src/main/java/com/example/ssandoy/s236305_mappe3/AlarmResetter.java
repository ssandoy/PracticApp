package com.example.ssandoy.s236305_mappe3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmResetter extends BroadcastReceiver {
    public AlarmResetter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmService.class);
        service.setAction(AlarmService.CREATE);
        context.startService(service);
    }
}
