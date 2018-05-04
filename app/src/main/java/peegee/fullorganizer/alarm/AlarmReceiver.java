package peegee.fullorganizer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.List;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.firebase_db.AlarmDB;

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

        final String alarmId = intent.getStringExtra("ID");
        final boolean reminder = intent.getBooleanExtra("REMINDER", false);
        int alarmRequestCode = intent.getIntExtra("REQUEST_CODE", -1);

        if (reminder) {
            Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
            serviceIntent.putExtra("REQUEST_CODE", alarmRequestCode)
                         .putExtra("ID", alarmId)
                         .putExtra("REMINDER", true);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        else {
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((AlarmDB) sample).getAlarmId().equals(alarmId);
                }
            };
            List<AlarmDB> evaluateResult = (List<AlarmDB>) CollectionUtils.select(MainActivity.alarmsList, condition);
            AlarmDB alarmDB = evaluateResult.get(0);

            int index = MainActivity.alarmsList.indexOf(alarmDB);
            boolean alarmOn = MainActivity.alarmsList.get(index).alarmOn;

            if (alarmOn) {
                Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
                serviceIntent.putExtra("REQUEST_CODE", alarmRequestCode)
                             .putExtra("ID", alarmId);
                ContextCompat.startForegroundService(context, serviceIntent);
            }
        }
    }
}
