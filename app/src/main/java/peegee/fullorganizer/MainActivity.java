/**
 * Final Project
 * Owner: Pierre Ghaly
 * Professor: Constandinos Mavromoustakis
 * Student ID: U153N0003
 * Description: A full organizer which includes: alarm, notes, to do list, reminders, tasks/classes and calendar
 */

package peegee.fullorganizer;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.alarm.AlarmActivity;
import peegee.fullorganizer.notes.NotesActivity;
import peegee.fullorganizer.reminder.ReminderActivity;
import peegee.fullorganizer.room_db.AppDatabase;
import peegee.fullorganizer.todo.TodoActivity;

/**
 * Main Activity
 * Loads the main UI
 *
 * TODO Colors, Strings, Dimens
 */
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.btnAlarm)
    ImageButton btnAlarm;
    @InjectView(R.id.btnNotes)
    ImageButton btnNotes;
    @InjectView(R.id.btnToDo)
    ImageButton btnToDo;
    @InjectView(R.id.btnReminders)
    ImageButton btnReminders;
    @InjectView(R.id.btnClasses)
    ImageButton btnClasses;
    @InjectView(R.id.btnCalender)
    ImageButton btnCalender;

    public static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "organizer_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * onClick method for the buttons
     * @param view the UI view
     */
    @OnClick({R.id.btnAlarm, R.id.btnNotes, R.id.btnToDo, R.id.btnReminders, R.id.btnClasses, R.id.btnCalender})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnAlarm:
                startActivity(new Intent(this, AlarmActivity.class));
                break;
            case R.id.btnNotes:
                startActivity(new Intent(this, NotesActivity.class));
                break;
            case R.id.btnToDo:
                startActivity(new Intent(this, TodoActivity.class));
                break;
            case R.id.btnReminders:
                startActivity(new Intent(this, ReminderActivity.class));
                break;
            case R.id.btnClasses:
                break;
            case R.id.btnCalender:
                break;
        }
    }
}
