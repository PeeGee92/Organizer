package peegee.fullorganizer.firebase_db;

/**
 * Notes database item class for Firebase Database
 */
public class NotesDB {

    private String noteId;
    private String uid;
    public String noteTitle;
    public String noteText;

    // Required for FireBase
    /**
     * Default Constructor
     */
    public NotesDB() {
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Non-default Constructor
     * <p>
     * @param noteTitle
     * @param noteText
     */
    public NotesDB(String noteTitle, String noteText) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
    }
}
