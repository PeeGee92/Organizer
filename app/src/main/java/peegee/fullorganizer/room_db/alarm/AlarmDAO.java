package peegee.fullorganizer.room_db.alarm;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AlarmDAO {

    @Query("SELECT * FROM AlarmDB")
    List<AlarmDB> getAll();

    @Query("SELECT * FROM AlarmDB WHERE alarmId = :id")
    AlarmDB getById(int id);

    @Insert
    void insert(AlarmDB alarmDB);

    @Update
    void update(AlarmDB alarmDB);

    @Delete
    void delete(AlarmDB alarmDB);
}
