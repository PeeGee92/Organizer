package peegee.fullorganizer.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import peegee.fullorganizer.R;

/**
 * RingtonePlayingService extending Service
 * Plays the ringtone service or stops it
 * and shows a notification
 */
public class RingtonePlayingService extends Service {

    MediaPlayer player;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("RingTone onStartCommand:", "In!");

        boolean alarmOn = intent.getExtras().getBoolean("alarm on");

        if (alarmOn && !isRunning) {

            player = MediaPlayer.create(this, R.raw.ringtone);
            player.start();

            this.isRunning = true;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent (this.getApplicationContext(), AlarmActivity.class);
            PendingIntent pendingNotificationIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);

            Intent snoozeIntent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
            //snoozeIntent.setAction();
            //snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent pendingSnoozeIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

            /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.alarm)
                    .setContentTitle("Alarm")
                    .setContentText("Click here to ...")
                    .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(AlarmActivity.class);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent pendingStackIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingStackIntent);

            notificationManager.notify(0, builder.build());*/

            /*Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText("Click here to ...")
                    .setSmallIcon(R.drawable.alarm)
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent)
                    //.addAction(0, "Snooze", pendingSnoozeIntent)
                    .build();

            notificationManager.notify(0, notification);*/

        }
        else if (!alarmOn && isRunning) {

            player.stop();

            this.isRunning = false;

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "OnDestroy Called", Toast.LENGTH_SHORT).show();
    }

    // Notification Channel
    // CHECK
    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("0", name, importance);
            // Register the channel with the system
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            //notificationManager.createNotificationChannel(channel);
        }
    }
}
