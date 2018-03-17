package peegee.fullorganizer.reminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.reminder.RemindersDB;
import peegee.fullorganizer.service.RemindersAdapter;

public class ReminderActivity extends AppCompatActivity {

    @InjectView(R.id.rvReminders)
    RecyclerView rvReminders;

    RecyclerView.Adapter adapter;

    List<RemindersDB> remindersDBList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.inject(this);

        // Database
        remindersDBList = MainActivity.db.remindersDAO().getAll();

        // RecyclerView setup
        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RemindersAdapter(remindersDBList);
        rvReminders.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
