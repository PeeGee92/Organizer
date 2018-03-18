package peegee.fullorganizer.reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.reminder.RemindersDB;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        ButterKnife.inject(this);

        // Get Intent extra to know which reminder to load
        // or to start a new reminder
        Intent intent = getIntent();
        id = intent.getIntExtra("REMINDER_ID", -1);

        if (id != -1) {
            // Database
            RemindersDB remindersDB = MainActivity.db.remindersDAO().getById(id);
            // TODO update data in layout
//            etTitle.setText(notesDB.getNoteTitle());
//            etNote.setText(notesDB.getNoteText());
            update = true; // It's an opened reminder so save should just update
        }

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
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);


        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);

                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getApplicationContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Pick Time");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void showDatePicker() {
        final Calendar myCalender = Calendar.getInstance();
        int day = myCalender.get(Calendar.DAY_OF_MONTH);
        int month = myCalender.get(Calendar.MONTH);
        int year = myCalender.get(Calendar.YEAR);


        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (datePicker.isShown()) {
                    myCalender.set(Calendar.DAY_OF_MONTH, day);
                    myCalender.set(Calendar.MONTH, month);
                }
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day);
        datePickerDialog.setTitle("Pick Date");
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.show();
    }

    @OnClick({R.id.btnSetDate, R.id.btnSetTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSetDate:
                showDatePicker();
                break;
            case R.id.btnSetTime:
                showTimePicker();
                break;
        }
    }
}
