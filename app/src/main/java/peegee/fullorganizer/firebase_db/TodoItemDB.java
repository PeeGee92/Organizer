package peegee.fullorganizer.firebase_db;

/**
 * To-do item database item class for Firebase Database
 */
public class TodoItemDB {

    private String listId;
    private String itemId;
    public boolean done;
    public String todoDescription;

    // Required for FireBase
    /**
     * Default Constructor
     */
    public TodoItemDB() {
    }

    /**
     * Non-default Constructor
     * <p>
     * @param done
     * @param todoDescription
     */
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
