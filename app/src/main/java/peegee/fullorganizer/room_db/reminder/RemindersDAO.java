package peegee.fullorganizer.room_db.reminder;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RemindersDAO {

    @Query("SELECT * FROM RemindersDB")
    List<RemindersDB> getAll();

    @Query("SELECT * FROM RemindersDB WHERE reminderId = :id")
    RemindersDB getById(int id);

    @Insert
    void insertAll(RemindersDB... remindersDBs);

    @Update
    void update(RemindersDB remindersDB);

    @Delete
    void delete(RemindersDB remindersDB);

}
