package peegee.fullorganizer.service.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.service.local_db.AlarmItemDB;
import peegee.fullorganizer.service.local_db.AppDatabase;

/**
 * BootService extends IntentService
 * Called after a reboot to reset alarms
 */
class BootService extends IntentService {

    /**
     * Default constructor
     */
    public BootService() {
        super("BootService");
    }

    /**
     * onHandleIntent method
     * <p>
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent alarmIntent;
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "alarms_reset")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        List<AlarmItemDB> alarmItemDBList = db.alarmItemDAO().getAll();

        for (AlarmItemDB item: alarmItemDBList) {

            alarmIntent= new Intent(getApplicationContext(), AlarmReceiver.class)
                    .putExtra("ID", item.getId())
                    .putExtra("REQUEST_CODE", item.getRequestCode())
                    .putExtra("REMINDER", item.isReminder());
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), item.getRequestCode(),
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            calendar.setTime(item.getAlarmTime());
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        }

        stopSelf();

    }
}
