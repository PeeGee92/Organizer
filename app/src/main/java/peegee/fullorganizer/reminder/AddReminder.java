package peegee.fullorganizer.reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.reminder.RemindersDB;

// TODO Convert alarm Date to values and vice versa (implement a method)
public class AddReminder extends AppCompatActivity {

    RemindersDB reminderDB;

    boolean update = false;
    int id;
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

    SimpleDateFormat timeFormat, dateFormat;
    Date date,time, alarmDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        ButterKnife.inject(this);

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        timeFormat = new SimpleDateFormat("hh:mm a");

        // Get Intent extra to know which reminder to load
        // or to start a new reminder
        Intent intent = getIntent();
        id = intent.getIntExtra("REMINDER_ID", -1);

        if (id != -1) {
            // Database
            reminderDB = MainActivity.db.remindersDAO().getById(id);
            // TODO Check for null values
            etTitle.setText(reminderDB.getReminderTitle());
            etLocation.setText(reminderDB.getReminderLocation());
            etDescription.setText(reminderDB.getReminderDescription());
            date = reminderDB.getReminderDate();
            time = reminderDB.getReminderTime();
            tvDate.setText(dateFormat.format(date));
            tvTime.setText(timeFormat.format(time));
            cbAlarm.setChecked(reminderDB.isReminderAlarm());
            // TODO Set alarm time and values

            update = true; // It's an opened reminder so save should just update
        }

        date = Calendar.getInstance().getTime();
        time = Calendar.getInstance().getTime();
        tvDate.setText(dateFormat.format(date));
        tvTime.setText(timeFormat.format(time));

        // Spinner setup
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AddReminder.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.reminder_spinner));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlarm.setAdapter(spinnerAdapter);

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
        int hour = time.getHours();
        int minute = time.getMinutes();


        final TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute);
                time = myCalender.getTime();
            }
        };
        final TimePickerDialog timePickerDialog = new TimePickerDialog(AddReminder.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Pick Time");
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tvTime.setText(timeFormat.format(time));
            }
        });
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void showDatePicker() {
        final Calendar myCalender = Calendar.getInstance();
        Log.d("DATE", "date: " + date);
        Log.d("DATE", "dateFormat: " + dateFormat.format(date));
        myCalender.setTime(date);
        int year = myCalender.get(Calendar.YEAR);
        int month = myCalender.get(Calendar.MONTH);
        int day = myCalender.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "year: " + year + " month: " + month + " day: " + day);


        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalender.set(Calendar.DAY_OF_MONTH, day);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.YEAR, year);

                date = myCalender.getTime();
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminder.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day);
        datePickerDialog.setTitle("Pick Date");
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tvDate.setText(dateFormat.format(date));
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

        if (update) {
            reminderDB.setReminderTitle(etTitle.getText().toString());
            reminderDB.setReminderLocation(etLocation.getText().toString());
            reminderDB.setReminderDescription(etDescription.getText().toString());
            reminderDB.setReminderDate(date);
            reminderDB.setReminderTime(time);
            reminderDB.setReminderAlarm(cbAlarm.isChecked());
            if (cbAlarm.isChecked())
                reminderDB.setReminderAlarmDate(alarmDate);

            MainActivity.db.remindersDAO().update(reminderDB);
        }
        else {

            reminderDB = new RemindersDB(etTitle.getText().toString(),
                    etLocation.getText().toString(),
                    etDescription.getText().toString(),
                    date, time,
                    cbAlarm.isChecked(), alarmDate);

            MainActivity.db.remindersDAO().insertAll(reminderDB);
        }

        startActivity(new Intent(AddReminder.this, ReminderActivity.class));
    }

    private void cancelReminder() {
        startActivity(new Intent(AddReminder.this, ReminderActivity.class));
    }
}
