package peegee.fullorganizer.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.util.Calendar;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.AlarmDB;
import peegee.fullorganizer.firebase_db.ReminderDB;
import peegee.fullorganizer.service.alarm.AlarmReceiver;

public class AddAlarm extends AppCompatActivity {

    @InjectView(R.id.timePicker)
    TimePicker timePicker;
    @InjectView(R.id.rbRepeat)
    RadioButton rbRepeat;
    @InjectView(R.id.rbOnce)
    RadioButton rbOnce;
    @InjectView(R.id.etSnooze)
    EditText etSnooze;
    @InjectView(R.id.btnAddAlarm)
    Button btnAddAlarm;
    @InjectView(R.id.btnCancelAlarm)
    Button btnCancel;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    AlarmManager alarmManager;
    NotificationManager notificationManager;
    Calendar calendar;

    List<AlarmDB> evaluateResult; // Used to retrieve item by id
    AlarmDB alarmDB;

    String id;
    boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
            }
        });

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        id = intent.getStringExtra("ALARM_ID");

        if(id != null)
        {
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((AlarmDB)sample).getAlarmId().equals(id);
                }
            };
            evaluateResult = (List<AlarmDB>) CollectionUtils.select( MainActivity.alarmsList, condition );
            alarmDB = evaluateResult.get(0);
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(alarmDB.alarmDate);

            timePicker.setHour(tempCal.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(tempCal.get(Calendar.MINUTE));
            if (alarmDB.alarmRepeated) {
                rbRepeat.setChecked(true);
            } else {
                rbOnce.setChecked(true);
            }
            etSnooze.setText(Integer.toString(alarmDB.alarmSnooze));

            update = true; // It's an already saved alarm so save should just update
        }
    }

    @OnClick({R.id.btnAddAlarm, R.id.btnCancelAlarm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddAlarm:
                addAlarm();
                break;
            case R.id.btnCancelAlarm:
                startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
                break;
        }
    }

    synchronized private void addAlarm() {
        if(etSnooze.getText().toString().trim().isEmpty()) {
            etSnooze.setText("0");
        }

        int alarmRequestCode;

        Calendar tempCal = Calendar.getInstance();
        tempCal.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        tempCal.set(Calendar.MINUTE,timePicker.getMinute());

        // Firebase
        synchronized (MainActivity.FBLOCK) {
            if (update) {

                // Cancel previous broadcast
                alarmRequestCode = alarmDB.getAlarmRequestCode();

                // Used to cancel alarm
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent cancelIntent = new Intent(getApplicationContext(), AlarmReceiver.class)
                        .putExtra("ID", alarmDB.getAlarmId())
                        .putExtra("REQUEST_CODE", alarmRequestCode);
                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(cancelPendingIntent);

                alarmDB.alarmRepeated = rbRepeat.isChecked();
                alarmDB.alarmDate = tempCal.getTime();
                alarmDB.alarmSnooze = Integer.parseInt(etSnooze.getText().toString());
                synchronized (MainActivity.FBLOCK) {
                    MainActivity.alarmRef.child(alarmDB.getAlarmId()).setValue(alarmDB);
                }
            } else {
                alarmDB = new AlarmDB(rbRepeat.isChecked(),
                        Integer.parseInt(etSnooze.getText().toString()),
                        tempCal.getTime(),
                        true);
                alarmDB.setUid(MainActivity.getCurrentUid());
                alarmRequestCode = MainActivity.getRequestCode();
                alarmDB.setAlarmRequestCode(alarmRequestCode);
                synchronized (MainActivity.FBLOCK) {
                    id = MainActivity.alarmRef.push().getKey();
                    alarmDB.setAlarmId(id);
                    MainActivity.alarmRef.child(id).setValue(alarmDB);
                }
            }
        }

        calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        if(calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class)
                .putExtra("ID", id)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
    }

    public static void cancelOldAndSetNew(AlarmDB alarmDB, Calendar calendar, Context appContext,
                                          AlarmManager alarmManager, NotificationManager notificationManager) {

        cancelAlarm(alarmDB, appContext, alarmManager, notificationManager);

        String alarmId = alarmDB.getAlarmId();
        int alarmRequestCode = alarmDB.getAlarmRequestCode();

        Intent intent = new Intent(appContext, AlarmReceiver.class)
                .putExtra("ID", alarmId)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(AlarmDB alarmDB, Context appContext,
                                   AlarmManager alarmManager, NotificationManager notificationManager) {
        String alarmId = alarmDB.getAlarmId();
        int alarmRequestCode = alarmDB.getAlarmRequestCode();

        Intent cancelIntent = new Intent(appContext, AlarmReceiver.class)
                .putExtra("ID", alarmId)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(appContext, alarmRequestCode, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(cancelPendingIntent);
        notificationManager.cancel(alarmRequestCode);
    }

    public static void setReminderAlarm(ReminderDB reminderDB, Context appContext,
                                        AlarmManager alarmManager) {
        String id = reminderDB.getReminderId();
        int alarmRequestCode = reminderDB.getAlarmRequestCode();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reminderDB.reminderAlarmDate);

        Intent intent = new Intent(appContext, AlarmReceiver.class)
                .putExtra("REMINDER", true)
                .putExtra("ID", id)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarmRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelReminderAlarm(ReminderDB reminderDB, Context appContext,
                                           AlarmManager alarmManager, NotificationManager notificationManager) {

        String id = reminderDB.getReminderId();
        int alarmRequestCode = reminderDB.getAlarmRequestCode();

        Intent cancelIntent = new Intent(appContext, AlarmReceiver.class)
                .putExtra("REMINDER", true)
                .putExtra("ID", id)
                .putExtra("REQUEST_CODE", alarmRequestCode);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(appContext, alarmRequestCode, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(cancelPendingIntent);
        notificationManager.cancel(alarmRequestCode);
    }
}
