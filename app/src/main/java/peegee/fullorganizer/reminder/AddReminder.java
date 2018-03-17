package peegee.fullorganizer.reminder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.reminder.RemindersDB;

public class AddReminder extends AppCompatActivity {

    RemindersDB reminderDB;

    boolean update = false;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Get Intent extra to know which reminder to load
        // or to start a new reminder
        Intent intent = getIntent();
        id = intent.getIntExtra("REMINDER_ID", -1);

        if(id != -1)
        {
            // Database
            RemindersDB remindersDB = MainActivity.db.remindersDAO().getById(id);
            // TODO update data in layout
//            etTitle.setText(notesDB.getNoteTitle());
//            etNote.setText(notesDB.getNoteText());
            update = true; // It's an opened reminder so save should just update
        }
    }
}
