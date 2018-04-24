package peegee.fullorganizer.firebase_db;

import java.util.ArrayList;
import java.util.List;

public class TodoListDB {

    private String todoListId;
    public String todoListTitle;
    public List<TodoItemDB> todoItemList;

    // Required for FireBase
    public TodoListDB() {
    }

    public TodoListDB(String todoListTitle) {
        this.todoListTitle = todoListTitle;
        this.todoItemList = new ArrayList<>();
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }
}
