package peegee.fullorganizer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * AlarmReceiver extending BroadcastReceiver
 * Gets the alarm from AddAlarm and start ringtone service
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver on Receive:", "In!");

        boolean alarmOn = intent.getExtras().getBoolean("alarm on");

        // For test purposes
        //Toast.makeText(context, "Alarm triggered", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(context, RingtonePlayingService.class).putExtra("alarm on", alarmOn);
        context.startService(serviceIntent);
    }
}
