package peegee.fullorganizer.service.alarm;

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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.alarm.AlarmActivity;
import peegee.fullorganizer.firebase_db.AlarmDB;
import peegee.fullorganizer.firebase_db.ReminderDB;
import peegee.fullorganizer.reminder.AddReminder;

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
    NotificationCompat.Action snoozeAction;
    NotificationCompat.Action deleteAction;
    NotificationCompat.Action dismissAction;

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
            if (intent.getBooleanExtra("REMINDER", false))
                initReminderNotification();
            else
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
                case "dismiss":
                    alarmManager.cancel(cancelPendingIntent);
                    player.stop();
                    changeAlarmValuesToOff();
                    stopSelf();
                    break;
                case "snooze":
                    alarmManager.cancel(cancelPendingIntent);
                    player.stop();
                    setNewSnoozeAlarm();
                    stopSelf();
                    break;
                case "reminder_click":
                case "reminder_delete":
                    cancelIntent.putExtra("REMINDER", true);
                    cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                            alarmRequestCode,
                            cancelIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(cancelPendingIntent);
                    notificationManager.cancel(alarmRequestCode);
                    player.stop();

                    Predicate condition = new Predicate() {
                        public boolean evaluate(Object sample) {
                            return ((ReminderDB) sample).getReminderId().equals(alarmId);
                        }
                    };
                    List<ReminderDB> evaluateResult = (List<ReminderDB>) CollectionUtils.select(MainActivity.reminderList, condition);
                    ReminderDB reminderDB = evaluateResult.get(0);

                    int index = MainActivity.reminderList.indexOf(reminderDB);
                    MainActivity.reminderList.get(index).reminderAlarm = false;
                    MainActivity.reminderList.get(index).reminderAlarmValue = 0;
                    MainActivity.reminderList.get(index).reminderAlarmType = "Minutes";

                    if (action.equals("reminder_click")) {
                        Intent reminderIntent = new Intent(this, AddReminder.class)
                                .putExtra("REMINDER_ID", alarmId);
                        startActivity(reminderIntent);
                    }

                    stopSelf();
            }
        }
        else { // New Alarm to start
            player = MediaPlayer.create(this, R.raw.ringtone);
            player.start();
            showNotification();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        notificationManager.cancelAll();
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
            calendar.add(Calendar.DATE, 1);
            MainActivity.alarmsList.get(index).alarmDate = calendar.getTime();

            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class)
                    .putExtra("ID", alarmId)
                    .putExtra("REQUEST_CODE", alarmRequestCode);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            synchronized (MainActivity.FBLOCK) {
                MainActivity.alarmRef.child(alarmId).setValue(MainActivity.alarmsList.get(index));
            }
        }
        else {
            MainActivity.alarmsList.get(index).alarmOn = false;
            synchronized (MainActivity.FBLOCK) {
                MainActivity.alarmRef.child(alarmId).setValue(MainActivity.alarmsList.get(index));
            }
        }

        if (!action.equals("dismiss")) {
            startActivity(new Intent(getApplicationContext(), AlarmActivity.class));
        }
    }

    private void initReminderNotification() {
        setNotificationChannel();

        Predicate condition = new Predicate() {
            public boolean evaluate(Object sample) {
                return ((ReminderDB) sample).getReminderId().equals(alarmId);
            }
        };
        List<ReminderDB> evaluateResult = (List<ReminderDB>) CollectionUtils.select(MainActivity.reminderList, condition);
        ReminderDB reminderDB = evaluateResult.get(0);

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());

        Intent click_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "reminder_click")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent click_pending = PendingIntent.getService(getApplicationContext(), MainActivity.getRequestCode(), click_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent delete_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "reminder_delete")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent delete_pending = PendingIntent.getService(getApplicationContext(), MainActivity.getRequestCode(), delete_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        deleteAction = new NotificationCompat.Action(R.drawable.small_delete,
                "Dismiss", delete_pending);

        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(reminderDB.reminderTitle)
                .setContentText("Click the notification to open reminder")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(click_pending)
                .addAction(deleteAction)
                .setOngoing(false)
                .setAutoCancel(true);
    }

    private void initNotification() {
        setNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());

        Intent click_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "click")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent click_pending = PendingIntent.getService(getApplicationContext(), MainActivity.getRequestCode(), click_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent snooze_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "snooze")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent snooze_pending = PendingIntent.getService(getApplicationContext(), MainActivity.getRequestCode(), snooze_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        snoozeAction = new NotificationCompat.Action(R.drawable.bell_ring,
                "Snooze", snooze_pending);

        Intent dismiss_intent = new Intent(getApplicationContext(), RingtonePlayingService.class)
                .putExtra("intent_action", "dismiss")
                .putExtra("REQUEST_CODE", alarmRequestCode)
                .putExtra("ID", alarmId);
        PendingIntent dismiss_pending = PendingIntent.getService(getApplicationContext(), MainActivity.getRequestCode(), dismiss_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        dismissAction = new NotificationCompat.Action(R.drawable.bell_ring,
                "Dismiss", dismiss_pending);

        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm On!")
                .setContentText("Click the notification to dismiss and open alarm")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(click_pending)
                .setDeleteIntent(click_pending)
                .addAction(dismissAction)
                .addAction(snoozeAction)
                .setOngoing(false)
                .setAutoCancel(true);
    }

    private void showNotification() {
        notificationManager.notify(alarmRequestCode, notificationBuilder.build());
    }

    private void setNotificationChannel() {
        channelId = "channel_id";
        channelName = "alarm_notification_channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(notificationChannel);
    }
}
