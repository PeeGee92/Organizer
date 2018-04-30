package peegee.fullorganizer.alarm;

import android.app.AlarmManager;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Calendar;
import java.util.List;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.AlarmDB;

/**
 * RingtonePlayingService extending Service
 * Plays the ringtone service or stops it
 * and shows a notification
 */
public class RingtonePlayingService extends Service {

    private String alarmId;
    private int alarmRequestCode;
    String action;
    AlarmManager alarmManager;
    Intent cancelIntent;
    PendingIntent cancelPendingIntent;

    // Player
    MediaPlayer player;

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

        alarmRequestCode = intent.getIntExtra("REQUEST_CODE", -1);
        alarmId = intent.getStringExtra("ID");
        action = intent.getStringExtra("intent_action");

        // Used to cancel alarm
        if (action != null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            cancelIntent = new Intent(getApplicationContext(), AlarmReceiver.class)
                    .putExtra("ID", alarmId)
                    .putExtra("REQUEST_CODE", alarmRequestCode);
            cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (action != null) {
            switch (action) {
                case "click":
                    Log.d("Action", "click: " + action);
                    alarmManager.cancel(cancelPendingIntent);
                    player.stop();
                    changeAlarmValuesToOff();
                    break;
                case "snooze":
                    Log.d("Action", "snooze: " + action);
                    alarmManager.cancel(cancelPendingIntent);
                    player.stop();
                    setNewSnoozeAlarm();
                    break;
                case "dismiss":
                    Log.d("Action", "dismiss: " + action);
                    alarmManager.cancel(cancelPendingIntent);
                    player.stop();
                    changeAlarmValuesToOff();
                    break;
                default:
            }
        }
        else { // New Alarm to start
            player = MediaPlayer.create(this, R.raw.ringtone);
            player.start();
            showNotification();
        }

        return START_STICKY;
    }

    private void setNewSnoozeAlarm() {
        Intent snoozeIntent = new Intent(getApplicationContext(), AlarmReceiver.class)
                .putExtra("ID", alarmId)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        Predicate condition = new Predicate() {
            public boolean evaluate(Object sample) {
                return ((AlarmDB)sample).getAlarmId().equals(alarmId);
            }
        };
        List<AlarmDB> evaluateResult = (List<AlarmDB>) CollectionUtils.select( MainActivity.alarmsList, condition );
        AlarmDB alarmDB = evaluateResult.get(0);

        int index = MainActivity.alarmsList.indexOf(alarmDB);
        int snooze = MainActivity.alarmsList.get(index).alarmSnooze;
        calendar.add(Calendar.MINUTE, snooze);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), snoozePendingIntent);
    }

    private void changeAlarmValuesToOff() {
        // TODO
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
                .setContentTitle("Alarm On!")
                .setContentText("Click the notification to dismiss")
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
