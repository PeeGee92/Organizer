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

//        isRunning = player.isPlaying();

        Log.d("Alarm RingTone onStartCommand:", "In!");

        boolean alarmOn = intent.getExtras().getBoolean("ALARM_ON");

        Log.d("Alarm RingTone onStartCommand:", "alarm on: " + alarmOn);
        Log.d("Alarm RingTone onStartCommand:", "isRunning: " + isRunning);

        if (alarmOn && !isRunning) {

            player = MediaPlayer.create(this, R.raw.ringtone);
            player.start();

            this.isRunning = true;

        }
        else if (!alarmOn && isRunning) {

            player.stop();

            this.isRunning = false;

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO
    }
}
