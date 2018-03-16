package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class TodoListDB {

    public TodoListDB(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }

    @PrimaryKey(autoGenerate = true)
    private int todoListId;

    @ColumnInfo(name = "todo_list_title")
    private String todoListTitle;

    public int getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(int todoListId) {
        this.todoListId = todoListId;
    }

    public String getTodoListTitle() {
        return todoListTitle;
    }

    public void setTodoListTitle(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }
}
