package peegee.fullorganizer.room_db.todo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TodoDAO {

    @Query("SELECT * FROM TodoDB WHERE listId = :id")
    List<TodoDB> loadByListId(int id);

    @Query("SELECT * FROM TodoDB WHERE listId = :id AND todo_done LIKE :done")
    List<TodoDB> findByDone(int id, boolean done);

    @Insert
    void insertAll(List<TodoDB> todoDBs);

    @Delete
    void delete(TodoDB todoDB);

    @Update
    void update(TodoDB todoDB);

}
