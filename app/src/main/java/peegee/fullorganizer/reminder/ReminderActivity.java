package peegee.fullorganizer.reminder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.service.adapters.RemindersAdapter;

/**
 * Reminder Activity
 * The main activity for the reminder function
 * Launched once the corresponding image button is pressed in Main Activity
 * or when notification is pressed
 */
public class ReminderActivity extends AppCompatActivity {

    @InjectView(R.id.rvReminders)
    RecyclerView rvReminders;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.Adapter adapter;

    /**
     * onCreate method
     * <p>
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReminderActivity.this, MainActivity.class));
            }
        });

        // RecyclerView setup
        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RemindersAdapter(MainActivity.reminderList);
        rvReminders.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReminderActivity.this, AddReminder.class));
            }
        });
    }

}
