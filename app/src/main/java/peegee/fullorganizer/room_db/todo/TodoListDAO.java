package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TodoListDAO {

    @Query("SELECT * FROM TodoListDB")
    List<TodoListWithItems> loadListWithItems();

}
