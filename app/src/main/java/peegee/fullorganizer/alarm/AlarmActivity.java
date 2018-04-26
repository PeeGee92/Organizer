package peegee.fullorganizer.alarm;

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
import peegee.fullorganizer.service.adapters.AlarmAdapter;


/**
 * Alarm activity
 * Loaded when the alarm button is pressed from main activity
 * or when notification is clicked
 * <p>
 * MISSING:
 * Play alarm even if the app is closed or minimized
 * Notification
 * Snooze and Stop buttons in notifications
 * Choose the ringtones from the ones on the phone itself
 * Repeated or Just Once alarm
 */
public class AlarmActivity extends AppCompatActivity {

    @InjectView(R.id.rvAlarm)
    RecyclerView rvAlarm;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlarmActivity.this, MainActivity.class));
            }
        });

        // RecyclerView setup
        rvAlarm.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(MainActivity.alarmsList);
        rvAlarm.setAdapter(adapter);

        // Used to load a new activity to add a new alarm
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmActivity.this, AddAlarm.class);
                startActivity(intent);
            }
        });
    }

}
