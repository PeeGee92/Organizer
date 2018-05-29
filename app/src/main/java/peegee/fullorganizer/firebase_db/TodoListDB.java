package peegee.fullorganizer.firebase_db;

/**
 * To-do list database item class for Firebase Database
 */
public class TodoListDB {

    private String uid;
    private String todoListId;
    public String todoListTitle;

    // Required for FireBase
    /**
     * Default Constructor
     */
    public TodoListDB() {
    }

    /**
     * Non-default Constructor
     * <p>
     * @param todoListTitle
     */
    public TodoListDB(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }
}
