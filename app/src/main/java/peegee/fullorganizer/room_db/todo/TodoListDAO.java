package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TodoListDAO {

    @Query("SELECT * FROM TodoListDB")
    List<TodoListDB> getAll();

    @Query("SELECT * FROM TodoListDB WHERE todoListId = :id")
    TodoListDB getListById(int id);

    @Insert
    long insert(TodoListDB todoListDB);

    @Update
    int update(TodoListDB todoListDB);

    @Delete
    void delete(TodoListDB todoListDB);

}
