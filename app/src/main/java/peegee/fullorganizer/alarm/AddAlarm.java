package peegee.fullorganizer.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.alarm.AlarmDAO;
import peegee.fullorganizer.room_db.alarm.AlarmDB;


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
    @InjectView(R.id.btnCancel)
    Button btnCancel;

    AlarmManager alarmManager;
    Calendar calendar;

    int id;
    boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        ButterKnife.inject(this);

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        id = intent.getIntExtra("NOTE_ID", -1);

        if(id != -1)
        {
            // Database
            AlarmDB alarmDB = MainActivity.db.alarmDAO().getById(id);
            timePicker.setHour(alarmDB.getAlarmHour());
            timePicker.setMinute(alarmDB.getAlarmMin());
            if (alarmDB.isAlarmRepeated()) {
                rbRepeat.setChecked(true);
            }
            else {
                rbOnce.setChecked(true);
            }
            etSnooze.setText(alarmDB.getAlarmSnooze());
            update = true; // It's an already saved alarm so save should just update
        }
    }

    @OnClick({R.id.btnAddAlarm, R.id.btnCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddAlarm:
                addAlarm();
                startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
                break;
            case R.id.btnCancel:
                startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
                break;
        }
    }

    private void addAlarm() {

        //Add to Database
        if (update) {
            AlarmDB alarmDB = MainActivity.db.alarmDAO().getById(id);
            MainActivity.db.alarmDAO().update(alarmDB);
        }
        else {
            AlarmDB alarmDB = new AlarmDB(timePicker.getHour(), timePicker.getMinute(),
                    Integer.parseInt(etSnooze.getText().toString()), rbRepeat.isChecked(), true);
            MainActivity.db.alarmDAO().insertAll(alarmDB);
        }

        // TODO check
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        Intent intent = new Intent(AddAlarm.this, AlarmReceiver.class)
                .putExtra("ALARM_ON", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
