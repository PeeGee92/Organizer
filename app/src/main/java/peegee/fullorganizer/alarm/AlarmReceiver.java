package peegee.fullorganizer.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import peegee.fullorganizer.R;
import peegee.fullorganizer.service.JobSchedulerService;

/**
 * AlarmReceiver extending BroadcastReceiver
 * Gets the alarm from AddAlarm and start ringtone service
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Reset Alarms after reboot
//        }


        boolean alarmOn = intent.getExtras().getBoolean("ALARM_ON");

        if (alarmOn) {
            // To avoid java.lang.IllegalStateException: Not allowed to start service Intent
            ComponentName alarmRingtoneService = new ComponentName(context, JobSchedulerService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, alarmRingtoneService)
                    .setPersisted(true)
                    .setOverrideDeadline(1 * 3000)
                    .build();

            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

            jobScheduler.schedule(jobInfo);
        }
    }
}
