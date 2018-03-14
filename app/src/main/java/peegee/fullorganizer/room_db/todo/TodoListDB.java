package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by GhalysOnly on 10/03/2018.
 */

@Entity
public class TodoListDB {

    public TodoListDB(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }

    @PrimaryKey(autoGenerate = true)
    private int todoListId;

    @ColumnInfo(name = "todo_list_title")
    private String todoListTitle;

    @ColumnInfo(name = "list_items_done")
    private int listItemsDone;

    @ColumnInfo(name = "list_items_not_done")
    private int listItemsNotDone;

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

    public int getListItemsDone() {
        return listItemsDone;
    }

    public void setListItemsDone(int listItemsDone) {
        this.listItemsDone = listItemsDone;
    }

    public int getListItemsNotDone() {
        return listItemsNotDone;
    }

    public void setListItemsNotDone(int listItemsNotDone) {
        this.listItemsNotDone = listItemsNotDone;
    }
}
