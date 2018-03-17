package peegee.fullorganizer.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import peegee.fullorganizer.R;

/**
 * AlarmReceiver extending BroadcastReceiver
 * Gets the alarm from AddAlarm and start ringtone service
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver on Receive:", "In!");

//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Reset Alarms after reboot
//        }


        boolean alarmOn = intent.getExtras().getBoolean("ALARM_ON");

        // For test purposes
        //Toast.makeText(context, "Alarm triggered", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(context, RingtonePlayingService.class).putExtra("ALARM_ON", alarmOn);
        context.startService(serviceIntent);

        showNotification(context);
    }

    private void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm On")
                .setContentText("Click here to stop it!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        getNotificationChannel();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void getNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION_CHANNEL";
            String description = "ALARM_NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ALARM_CHANNEL", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
//            NotificationManager notificationManager = NotificationManagerCompat.from();
//            notificationManager.createNotificationChannel(channel);
        }
    }
}
