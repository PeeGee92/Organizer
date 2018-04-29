package peegee.fullorganizer.firebase_db;

import java.util.ArrayList;
import java.util.List;

public class TodoListDB {

    private String uid;
    private String todoListId;
    public String todoListTitle;

    // Required for FireBase
    public TodoListDB() {
    }

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
