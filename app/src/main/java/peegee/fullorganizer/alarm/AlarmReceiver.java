package peegee.fullorganizer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * AlarmReceiver extending BroadcastReceiver
 * Gets the alarm from AddAlarm and start ringtone service
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG  = "ALARM_JOB";

    @Override
    public void onReceive(final Context context, Intent intent) {

        // TODO
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Reset Alarms after reboot
//        }

        boolean alarmOn = intent.getExtras().getBoolean("ALARM_ON");

        if (alarmOn) {
            Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
