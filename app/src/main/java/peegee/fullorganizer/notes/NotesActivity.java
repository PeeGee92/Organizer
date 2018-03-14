package peegee.fullorganizer.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.notes.NotesDB;
import peegee.fullorganizer.service.NotesAdapter;

public class NotesActivity extends AppCompatActivity {

    @InjectView(R.id.rvNotes)
    RecyclerView rvNotes;

    RecyclerView.Adapter adapter;

    List<NotesDB> notesDBList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        ButterKnife.inject(this);

        // Database
        notesDBList = MainActivity.db.notesDAO().getAll();

        // RecyclerView setup
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotesAdapter(notesDBList);
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
