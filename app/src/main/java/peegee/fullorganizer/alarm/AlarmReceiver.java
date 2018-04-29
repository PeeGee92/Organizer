package peegee.fullorganizer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

/**
 * AlarmReceiver extending BroadcastReceiver
 * Gets the alarm from AddAlarm and start ringtone service
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        // TODO
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Reset Alarms after reboot
//        }

        boolean alarmOn = intent.getExtras().getBoolean("ALARM_ON");
        int id = intent.getIntExtra("ID", -1);

        if (alarmOn) {
            Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
            serviceIntent.putExtra("ID", id);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}
