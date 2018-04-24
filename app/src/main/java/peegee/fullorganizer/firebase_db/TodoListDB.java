package peegee.fullorganizer.firebase_db;

public class TodoListDB {

    public String todoListTitle;

    // Required for FireBase
    public TodoListDB() {
    }

    public TodoListDB(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }
}
