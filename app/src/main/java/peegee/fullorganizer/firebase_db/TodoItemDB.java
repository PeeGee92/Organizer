package peegee.fullorganizer.firebase_db;

public class TodoItemDB {

    private String listId;
    private String itemId;
    public boolean done;
    public String todoDescription;

    // Required for FireBase
    public TodoItemDB() {
    }

    public TodoItemDB(boolean done, String todoDescription) {
        this.done = done;
        this.todoDescription = todoDescription;
    }

    public String getItemId() {
        return itemId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
