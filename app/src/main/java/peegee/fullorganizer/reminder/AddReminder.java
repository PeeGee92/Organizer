package peegee.fullorganizer.reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.alarm.AddAlarm;
import peegee.fullorganizer.firebase_db.ReminderDB;
import peegee.fullorganizer.service.local_db.AlarmItemDB;
import peegee.fullorganizer.service.local_db.AppDatabase;

public class AddReminder extends AppCompatActivity {

    List<ReminderDB> evaluateResult; // Used to retrieve item by id
    ReminderDB reminderDB;

    boolean update = false;
    String id;

    @InjectView(R.id.etTitle)
    EditText etTitle;
    @InjectView(R.id.etLocation)
    EditText etLocation;
    @InjectView(R.id.btnSetDate)
    Button btnSetDate;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.btnSetTime)
    Button btnSetTime;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.cbAlarm)
    CheckBox cbAlarm;
    @InjectView(R.id.spAlarm)
    Spinner spAlarm;
    @InjectView(R.id.etAlarmTime)
    EditText etAlarmTime;
    @InjectView(R.id.etDescription)
    EditText etDescription;
    @InjectView(R.id.btnSaveReminder)
    Button btnSaveReminder;
    @InjectView(R.id.btnCancelReminder)
    Button btnCancelReminder;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    SimpleDateFormat timeFormat, dateFormat;
    Date reminderDate, alarmDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddReminder.this, ReminderActivity.class));
            }
        });

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        timeFormat = new SimpleDateFormat("hh:mm a");

        // Get Intent extra to know which reminder to load
        // or to start a new reminder
        Intent intent = getIntent();
        id = intent.getStringExtra("REMINDER_ID");

        // Spinner setup
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AddReminder.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.reminder_spinner));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlarm.setAdapter(spinnerAdapter);

        if (id != null) {
            // Firebase
            synchronized (MainActivity.FBLOCK) {
                Predicate condition = new Predicate() {
                    public boolean evaluate(Object sample) {
                        return ((ReminderDB)sample).getReminderId().equals(id);
                    }
                };
                evaluateResult = (List<ReminderDB>) CollectionUtils.select( MainActivity.reminderList, condition );
                reminderDB = evaluateResult.get(0);

                etTitle.setText(reminderDB.reminderTitle);
                etLocation.setText(reminderDB.reminderLocation);
                etDescription.setText(reminderDB.reminderDescription);
                reminderDate = reminderDB.reminderDate;
                tvDate.setText(dateFormat.format(reminderDate));
                tvTime.setText(timeFormat.format(reminderDate));
                cbAlarm.setChecked(reminderDB.reminderAlarm);
                if (cbAlarm.isChecked()) {
                    String type = reminderDB.reminderAlarmType;
                    int position = spinnerAdapter.getPosition(type);
                    spAlarm.setSelection(position);
                    etAlarmTime.setText(Integer.toString(reminderDB.reminderAlarmValue));
                }
            }
            update = true; // It's an opened reminder so save should just update
        }

        if (!update) {
            reminderDate = Calendar.getInstance().getTime();
            tvDate.setText(dateFormat.format(reminderDate));
            tvTime.setText(timeFormat.format(reminderDate));
        }

        // Initialize spinner enable/disable
        setEnableSpinner();

        // CheckBox onChangedListener
        cbAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setEnableSpinner();
            }
        });
    }

    private void setEnableSpinner() {

        boolean checked = cbAlarm.isChecked();

        // Change spinner
        spAlarm.setActivated(checked);
        spAlarm.setClickable(checked);
        spAlarm.setEnabled(checked);

        // Change edit text
        etAlarmTime.setEnabled(checked);

    }

    private void showTimePicker() {
        final Calendar myCalender = Calendar.getInstance();
        myCalender.setTime(reminderDate);
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);


        final TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute);
                reminderDate = myCalender.getTime();
            }
        };
        final TimePickerDialog timePickerDialog = new TimePickerDialog(AddReminder.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Pick Time");
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tvTime.setText(timeFormat.format(reminderDate));
            }
        });
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void showDatePicker() {
        final Calendar myCalender = Calendar.getInstance();
        myCalender.setTime(reminderDate);
        int year = myCalender.get(Calendar.YEAR);
        int month = myCalender.get(Calendar.MONTH);
        int day = myCalender.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalender.set(Calendar.DAY_OF_MONTH, day);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.YEAR, year);

                reminderDate = myCalender.getTime();
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminder.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day);
        datePickerDialog.setTitle("Pick Date");
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tvDate.setText(dateFormat.format(reminderDate));
            }
        });
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @OnClick({R.id.btnSetDate, R.id.btnSetTime, R.id.btnSaveReminder, R.id.btnCancelReminder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                showDatePicker();
                break;
            case R.id.btnSetTime:
                showTimePicker();
                break;
            case R.id.btnSaveReminder:
                saveReminder();
                break;
            case R.id.btnCancelReminder:
                cancelReminder();
                break;
        }
    }

    private void saveReminder() {

        // Check for null values
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setHint("Please choose a title for you reminder");
            etTitle.setHintTextColor(Color.RED);
            return;
        }

        synchronized (MainActivity.FBLOCK) {

            // Firebase
            if (update) {
                // Update alarm
                if (reminderDB.reminderAlarm) { // There was an alarm set before
                    AddAlarm.cancelReminderAlarm(reminderDB, getApplicationContext(),
                            (AlarmManager) getSystemService(Context.ALARM_SERVICE),
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                }

                if (cbAlarm.isChecked()) {
                    // Set the new alarm
                    if (etAlarmTime.getText().toString().trim().isEmpty()) {
                        etAlarmTime.setText("0");
                    }
                    reminderDB.reminderAlarmType = spAlarm.getSelectedItem().toString();
                    reminderDB.reminderAlarmValue = Integer.parseInt(etAlarmTime.getText().toString());
                    switchTimeToDate(reminderDB.reminderAlarmType, reminderDB.reminderAlarmValue);

                    reminderDB.reminderAlarmDate = alarmDate;

                    AddAlarm.setReminderAlarm(reminderDB, getApplicationContext(),
                            (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                }

                reminderDB.reminderTitle = etTitle.getText().toString();
                reminderDB.reminderLocation = etLocation.getText().toString();
                reminderDB.reminderDescription = etDescription.getText().toString();
                reminderDB.reminderDate = reminderDate;
                reminderDB.reminderAlarm = cbAlarm.isChecked();

                synchronized (MainActivity.FBLOCK) {
                    MainActivity.reminderRef.child(reminderDB.getReminderId()).setValue(reminderDB);
                }
            } else { // New alarm
                if (etAlarmTime.getText().toString().trim().isEmpty() && cbAlarm.isChecked()) {
                    etAlarmTime.setText("0");
                }
                switchTimeToDate(spAlarm.getSelectedItem().toString(), Integer.parseInt(etAlarmTime.getText().toString()));
                reminderDB = new ReminderDB(etTitle.getText().toString(),
                        etLocation.getText().toString(),
                        etDescription.getText().toString(),
                        reminderDate,
                        cbAlarm.isChecked(), alarmDate,
                        Integer.parseInt(etAlarmTime.getText().toString()), spAlarm.getSelectedItem().toString());
                reminderDB.setUid(MainActivity.getCurrentUid());
                reminderDB.setAlarmRequestCode(MainActivity.getRequestCode());

                synchronized (MainActivity.FBLOCK) {
                    String key = MainActivity.reminderRef.push().getKey();
                    reminderDB.setReminderId(key);
                    MainActivity.reminderRef.child(key).setValue(reminderDB);

                    // Add alarm
                    if (cbAlarm.isChecked()) {
                        AddAlarm.setReminderAlarm(reminderDB, getApplicationContext(),
                                (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                    }
                }
            }

            // Local db
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "alarms_reset")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            if (cbAlarm.isChecked()) {
                AlarmItemDB tempAlarmDb = db.alarmItemDAO().getAlarmById(reminderDB.getReminderId());
                if (tempAlarmDb == null) {
                    tempAlarmDb = new AlarmItemDB(reminderDB.getReminderId(), reminderDB.getAlarmRequestCode(), reminderDB.reminderAlarmDate, true);
                    db.alarmItemDAO().insert(tempAlarmDb);
                }
                else {
                    tempAlarmDb.setAlarmTime(reminderDB.reminderAlarmDate);
                    db.alarmItemDAO().update(tempAlarmDb);
                }
            }
            else {
                AlarmItemDB tempAlarmDb = db.alarmItemDAO().getAlarmById(reminderDB.getReminderId());
                if (tempAlarmDb != null) {
                    db.alarmItemDAO().delete(tempAlarmDb);
                }
            }
        }

        startActivity(new Intent(AddReminder.this, ReminderActivity.class));
    }

    private void switchTimeToDate(String type, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reminderDate);

        switch (type) {
            case "Minutes":
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - n);
                break;
            case "Hours":
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - n);
                break;
            case "Days":
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - n);
                break;
        }

        alarmDate = calendar.getTime();
    }

    private void cancelReminder() {
        startActivity(new Intent(AddReminder.this, ReminderActivity.class));
    }
}
