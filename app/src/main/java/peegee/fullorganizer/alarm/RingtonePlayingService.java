package peegee.fullorganizer.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import peegee.fullorganizer.R;

/**
 * RingtonePlayingService extending Service
 * Plays the ringtone service or stops it
 * and shows a notification
 */
public class RingtonePlayingService extends Service {

    private int id;

    // Player
    MediaPlayer player;
    boolean isRunning;

    // Notification
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    String channelId;
    CharSequence channelName;
    NotificationCompat.Action dismissAction;
    NotificationCompat.Action snoozeAction;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
        startForeground(1, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO use id to cancel alarm
        // TODO decrease unique ID value
        intent.getIntExtra("ID", -1);

        // TODO Check for actions
        String action = intent.getStringExtra("intent_action");
        if (action != null) {
            switch (action) {
                case "click":
                    Log.d("Action", "click: " + action);
                    break;
                case "snooze":
                    Log.d("Action", "snooze: " + action);
                    break;
                case "dismiss":
                    Log.d("Action", "dismiss: " + action);
                    break;
                default:
            }
        }


        // TODO Check conditions (on start, when playing, stop, ...)
        if (!isRunning) {

            player = MediaPlayer.create(this, R.raw.ringtone);
            player.start();

            this.isRunning = true;

            showNotification();
        }
        else if (isRunning) {

            player.stop();

            this.isRunning = false;

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO
    }

    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        setNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());

        Intent click_intent = new Intent(getApplicationContext(), AlarmActivity.class)
                .putExtra("intent_action", "click");
        PendingIntent onclick_intent = PendingIntent.getActivity(this, 1, click_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismiss_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "dismiss");
        PendingIntent dismiss_pending = PendingIntent.getService(getApplicationContext(),1, dismiss_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snooze_intent=new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "snooze");
        PendingIntent snooze_pending = PendingIntent.getService(getApplicationContext(),1, snooze_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        dismissAction = new NotificationCompat.Action(R.drawable.small_delete,
                "Dismiss", dismiss_pending);
        snoozeAction = new NotificationCompat.Action(R.drawable.bell_ring,
                "Snooze", snooze_pending);
        //

        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm On")
                .setContentText("Click here to stop it!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(onclick_intent)
                .setDeleteIntent(onclick_intent)
                .setOngoing(true)
                .setAutoCancel(true)
                .addAction(snoozeAction)
                .addAction(dismissAction);
    }

    private void showNotification() {

        notificationManager.notify(1, notificationBuilder.build());

    }

    private void setNotificationChannel() {
        channelId = "alarm_channel_id";
        channelName = "alarm_notification_channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
