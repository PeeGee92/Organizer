package peegee.fullorganizer.notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import peegee.fullorganizer.MainActivity;
import peegee.fullorganizer.R;
import peegee.fullorganizer.firebase_db.NotesDB;

public class AddNote extends AppCompatActivity {

    @InjectView(R.id.etTitle)
    EditText etTitle;
    @InjectView(R.id.etNote)
    EditText etNote;
    @InjectView(R.id.btnAddNote)
    Button btnAddNote;
    @InjectView(R.id.btnCancelNote)
    Button btnCancelNote;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    List<NotesDB> evaluateResult; // Used to retrieve item by id
    NotesDB notesDB;

    boolean update = false;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNote.this, NotesActivity.class));
            }
        });

        // Get Intent extra to know which note to load
        // or to start a new note
        Intent intent = getIntent();
        id = intent.getStringExtra("NOTE_ID");

        if(id != null)
        {
            Predicate condition = new Predicate() {
                public boolean evaluate(Object sample) {
                    return ((NotesDB)sample).getNoteId().equals(id);
                }
            };
            evaluateResult = (List<NotesDB>) CollectionUtils.select( MainActivity.notesList, condition );
            notesDB = evaluateResult.get(0);
            etTitle.setText(notesDB.noteTitle);
            etNote.setText(notesDB.noteText);
            update = true; // It's an opened note so save should just update
        }
    }

    @OnClick({R.id.btnAddNote, R.id.btnCancelNote})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddNote:
                saveToDB();
                break;
            case R.id.btnCancelNote:
                startActivity(new Intent(AddNote.this, NotesActivity.class));
                break;
        }
    }

    // Firebase
    synchronized private void saveToDB() {

        // Input errors
        if (etNote.getText().toString().trim().isEmpty()) {
            etNote.setHint("Please enter some text in note");
            etNote.setHintTextColor(Color.RED);
            return;
        }

        if (update) {
            notesDB.noteTitle = etTitle.getText().toString();
            notesDB.noteText = etNote.getText().toString();
            synchronized (MainActivity.FBLOCK) {
                MainActivity.notesRef.child(notesDB.getNoteId()).setValue(notesDB);
            }
        } else {
            String tempTitle = etTitle.getText().toString();
            String tempNote = etNote.getText().toString();
            notesDB = new NotesDB(tempTitle, tempNote);
            synchronized (MainActivity.FBLOCK) {
                String key = MainActivity.notesRef.push().getKey();
                MainActivity.notesRef.child(key).setValue(notesDB);
            }
        }

        startActivity(new Intent(AddNote.this, NotesActivity.class));
    }
}
