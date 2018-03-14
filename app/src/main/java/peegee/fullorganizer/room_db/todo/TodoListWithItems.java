package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class TodoListWithItems {

    @Embedded
    private TodoListDB list;

    @Relation(parentColumn = "todoListId", entityColumn = "todoId", entity = TodoDB.class)
    private List<TodoDB> listItems;

    public TodoListDB getList() {
        return list;
    }

    public void setList(TodoListDB list) {
        this.list = list;
    }

    public List<TodoDB> getListItems() {
        return listItems;
    }

    public void setListItems(List<TodoDB> listItems) {
        this.listItems = listItems;
    }
}
