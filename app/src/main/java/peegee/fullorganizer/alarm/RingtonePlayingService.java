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
import java.util.Date;
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
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        alarmRequestCode = intent.getIntExtra("REQUEST_CODE", -1);
        alarmId = intent.getStringExtra("ID");
        action = intent.getStringExtra("intent_action");

        if (action == null) {
            initNotification();
            startForeground(alarmRequestCode, notificationBuilder.build());
        }

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
                    alarmManager.cancel(cancelPendingIntent);
                    notificationManager.cancel(alarmRequestCode);
                    player.stop();
                    changeAlarmValuesToOff();
                    break;
                case "snooze":
                    alarmManager.cancel(cancelPendingIntent);
                    notificationManager.cancel(alarmRequestCode);
                    player.stop();
                    setNewSnoozeAlarm();
                    break;
                case "dismiss":
                    alarmManager.cancel(cancelPendingIntent);
                    notificationManager.cancel(alarmRequestCode);
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

        Predicate condition = new Predicate() {
            public boolean evaluate(Object sample) {
                return ((AlarmDB)sample).getAlarmId().equals(alarmId);
            }
        };
        List<AlarmDB> evaluateResult = (List<AlarmDB>) CollectionUtils.select( MainActivity.alarmsList, condition );
        AlarmDB alarmDB = evaluateResult.get(0);

        int index = MainActivity.alarmsList.indexOf(alarmDB);
        int snooze = MainActivity.alarmsList.get(index).alarmSnooze;

        if (snooze > 0) {
            Intent snoozeIntent = new Intent(getApplicationContext(), AlarmReceiver.class)
                    .putExtra("ID", alarmId)
                    .putExtra("REQUEST_CODE", alarmRequestCode);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, snooze);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), snoozePendingIntent);
        }
        else { // Just dismiss the alarm
            changeAlarmValuesToOff();
        }
    }

    private void changeAlarmValuesToOff() {
        Predicate condition = new Predicate() {
            public boolean evaluate(Object sample) {
                return ((AlarmDB)sample).getAlarmId().equals(alarmId);
            }
        };
        List<AlarmDB> evaluateResult = (List<AlarmDB>) CollectionUtils.select( MainActivity.alarmsList, condition );
        AlarmDB alarmDB = evaluateResult.get(0);

        int index = MainActivity.alarmsList.indexOf(alarmDB);
        boolean repeat = MainActivity.alarmsList.get(index).alarmRepeated;

        if (repeat) {
            Date date = MainActivity.alarmsList.get(index).alarmDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class)
                    .putExtra("ID", alarmId)
                    .putExtra("REQUEST_CODE", alarmRequestCode);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else {
            MainActivity.alarmsList.get(index).alarmOn = false;
            synchronized (MainActivity.FBLOCK) {
                MainActivity.alarmRef.child(alarmId).setValue(MainActivity.alarmsList.get(index));
            }
        }
    }

    private void initNotification() {
        setNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());

        Intent click_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "click")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent click_pending = PendingIntent.getService(this, MainActivity.getRequestCode(), click_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismiss_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "dismiss")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent dismiss_pending = PendingIntent.getService(getApplicationContext(),MainActivity.getRequestCode(), dismiss_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snooze_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "snooze")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent snooze_pending = PendingIntent.getService(getApplicationContext(),MainActivity.getRequestCode(), snooze_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        dismissAction = new NotificationCompat.Action(R.drawable.small_delete,
                "Dismiss", dismiss_pending);
        snoozeAction = new NotificationCompat.Action(R.drawable.bell_ring,
                "Snooze", snooze_pending);

        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm On!")
                .setContentText("Click the notification to dismiss")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(click_pending)
                .setDeleteIntent(click_pending)
                .addAction(dismissAction)
                .addAction(snoozeAction)
                .setAutoCancel(true);
    }

    private void showNotification() {

        notificationManager.notify(alarmRequestCode, notificationBuilder.build());

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
