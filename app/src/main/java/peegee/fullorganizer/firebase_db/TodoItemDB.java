package peegee.fullorganizer.firebase_db;

public class TodoItemDB {

    public boolean done;
    public String todoDescription;

    // Required for FireBase
    public TodoItemDB() {
    }

    public TodoItemDB(boolean done, String todoDescription) {
        this.done = done;
        this.todoDescription = todoDescription;
    }
}
