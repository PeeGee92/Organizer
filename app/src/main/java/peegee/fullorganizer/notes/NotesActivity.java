package peegee.fullorganizer.notes;

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
import peegee.fullorganizer.service.adapters.NotesAdapter;

/**
 * Notes Activity
 * The main activity for the notes function
 * Launched once the corresponding image button is pressed in Main Activity
 */
public class NotesActivity extends AppCompatActivity {

    @InjectView(R.id.rvNotes)
    RecyclerView rvNotes;
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
        setContentView(R.layout.activity_notes);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, MainActivity.class));
            }
        });

        // RecyclerView setup
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotesAdapter(MainActivity.notesList);
        rvNotes.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotesActivity.this, AddNote.class));
            }
        });
    }

}
