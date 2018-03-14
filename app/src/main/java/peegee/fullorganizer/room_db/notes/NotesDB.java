package peegee.fullorganizer.room_db.notes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by GhalysOnly on 09/03/2018.
 */

@Entity
public class NotesDB {

    public NotesDB(String noteTitle, String noteText) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
    }

    @PrimaryKey (autoGenerate = true)
    private int noteID;

    @ColumnInfo (name = "note_title")
    private String noteTitle;

    @ColumnInfo (name = "note_text")
    private String noteText;

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getNoteID() {

        return noteID;
    }
}
