package peegee.fullorganizer.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.AlarmDB;


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

        Calendar tempCal = Calendar.getInstance();
        tempCal.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        tempCal.set(Calendar.MINUTE,timePicker.getMinute());

        // Firebase
        synchronized (MainActivity.FBLOCK) {
            if (update) {

                // TODO Cancel broadcast and resend it

                alarmDB.alarmRepeated = rbRepeat.isChecked();
                alarmDB.alarmDate = tempCal.getTime();
                synchronized (MainActivity.FBLOCK) {
                    MainActivity.alarmRef.child(alarmDB.getAlarmId()).setValue(alarmDB);
                }
            } else {
                alarmDB = new AlarmDB(rbRepeat.isChecked(),
                        Integer.parseInt(etSnooze.getText().toString()),
                        tempCal.getTime(),
                        true);
                alarmDB.setUid(MainActivity.getCurrentUid());
                synchronized (MainActivity.FBLOCK) {
                    id = MainActivity.alarmRef.push().getKey();
                    MainActivity.alarmRef.child(id).setValue(alarmDB);
                }
            }
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        if(calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(AddAlarm.this, AlarmReceiver.class)
                .putExtra("ALARM_ON", true)
                .putExtra("ID", 00); // TODO Generate a unique ID
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        startActivity(new Intent(AddAlarm.this, AlarmActivity.class));
    }
}
