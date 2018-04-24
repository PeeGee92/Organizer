package peegee.fullorganizer.firebase_db;

public class NotesDB {

    private String noteId;
    public String noteTitle;
    public String noteText;

    // Required for FireBase
    public NotesDB() {
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public NotesDB(String noteTitle, String noteText) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
    }
}
