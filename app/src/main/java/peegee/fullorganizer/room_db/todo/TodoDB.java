package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by GhalysOnly on 09/03/2018.
 */

@Entity
public class TodoDB {

    public TodoDB(String todoDescription, boolean todoDone) {
        this.todoDescription = todoDescription;
        this.todoDone = todoDone;
    }

    @PrimaryKey (autoGenerate = true)
    private int todoId;
    private int listId;

    @ColumnInfo (name = "todo_description")
    private String todoDescription;

    @ColumnInfo (name = "todo_done")
    private boolean todoDone;

    public int getListId() {
        return listId;
    }

    public int getTodoId() {
        return todoId;
    }

    public String getTodoDescription() {
        return todoDescription;
    }

    public boolean isTodoDone() {
        return todoDone;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public void setTodoDescription(String todoDescription) {
        this.todoDescription = todoDescription;
    }

    public void setTodoDone(boolean todoDone) {
        this.todoDone = todoDone;
    }
}
