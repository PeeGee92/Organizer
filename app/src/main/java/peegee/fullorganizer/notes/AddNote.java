package peegee.fullorganizer.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.room_db.notes.NotesDB;

public class AddNote extends AppCompatActivity {

    @InjectView(R.id.etTitle)
    EditText etTitle;
    @InjectView(R.id.etNote)
    EditText etNote;
    @InjectView(R.id.btnAddNote)
    Button btnAddNote;
    @InjectView(R.id.btnCancelNote)
    Button btnCancelNote;

    NotesDB notesDB;

    boolean update = false;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.inject(this);

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        id = intent.getIntExtra("NOTE_ID", -1);

        if(id != -1)
        {
            // Database
            NotesDB notesDB = MainActivity.db.notesDAO().getById(id);
            etTitle.setText(notesDB.getNoteTitle());
            etNote.setText(notesDB.getNoteText());
            update = true; // It's an opened note so save should just update
        }
    }

    @OnClick({R.id.btnAddNote, R.id.btnCancelNote})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddNote:
                saveToDB();
                startActivity(new Intent(AddNote.this, NotesActivity.class));
                break;
            case R.id.btnCancelNote:
                startActivity(new Intent(AddNote.this, NotesActivity.class));
                break;
        }
    }

    private void saveToDB() {

        if (update) {
            notesDB = MainActivity.db.notesDAO().getById(id);
            notesDB.setNoteTitle(etTitle.getText().toString());
            notesDB.setNoteText(etNote.getText().toString());

            MainActivity.db.notesDAO().update(notesDB);
        }
        else {
            String tempTitle = etTitle.getText().toString();
            String tempNote = etNote.getText().toString();
            notesDB = new NotesDB(tempTitle, tempNote);

            MainActivity.db.notesDAO().insertAll(notesDB);
        }

    }
}
